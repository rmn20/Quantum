#!/bin/sh
# Build Quantum.jar / Quantum.jad from the sources in this repository.
#
#   sh tools/build-jar.sh
#
# Requirements: a javac. A real JDK on PATH is used when present, otherwise the
# script falls back to the JDK-8 compiler classes in tools/j2me-stubs/jdk
# (see tools/j2me-stubs/README.md).
#
# The JavaME platform classes (CLDC 1.1 / MIDP 2.0 / JSR-75 / JSR-135 / JSR-184)
# come from tools/j2me-stubs, so no proprietary Wireless Toolkit is needed to
# build. Output lands in dist/, which .gitignore already excludes.
#
# Environment:
#   MIDP_LIBS   ':' separated JavaME SDK jars (or class dirs) to compile
#               against, instead of the stubs in tools/j2me-stubs. With a real
#               Wireless Toolkit 2.5.2 in $WTK:
#                 MIDP_LIBS=$WTK/lib/cldcapi11.jar:$WTK/lib/midpapi21.jar:\
# $WTK/lib/mmapi.jar:$WTK/lib/jsr184.jar:$WTK/lib/jsr75.jar
#   PREVERIFY   path to the `preverify` tool (also picked up from PATH). A CLDC
#               device or the WTK emulator only installs preverified classes,
#               so a device build needs it; a desktop/stub build does not.
#
# Without MIDP_LIBS the classes are NOT preverified and the JAR is only good
# for desktop testing.
set -e

ROOT=`cd "$(dirname "$0")/.." && pwd`
cd "$ROOT"

STUB_SRC=tools/j2me-stubs/src
STUB_CLASSES=tools/j2me-stubs/classes
CLASSES=build/compiled
DIST=dist

# ---------------------------------------------------------------- javac
if command -v javac >/dev/null 2>&1; then
    JAVAC="javac"
elif [ -x "$HOME/.cache/bin/javac" ]; then
    JAVAC="$HOME/.cache/bin/javac"
else
    echo "no javac found: install a JDK or provide \$HOME/.cache/bin/javac" >&2
    exit 1
fi
echo "compiler: $($JAVAC -version 2>&1)"

# ---------------------------------------------------------------- platform API
if [ -n "$MIDP_LIBS" ]; then
    PLATFORM="$MIDP_LIBS"
    echo "platform API: \$MIDP_LIBS"
else
    if [ ! -f "$STUB_CLASSES/javax/microedition/midlet/MIDlet.class" ]; then
        echo "compiling JavaME API stubs (no \$MIDP_LIBS given)"
        rm -rf "$STUB_CLASSES" && mkdir -p "$STUB_CLASSES"
        $JAVAC -nowarn -source 1.3 -target 1.1 -bootclasspath "" -d "$STUB_CLASSES" \
            `find "$STUB_SRC" -name '*.java'`
    fi
    PLATFORM="$STUB_CLASSES"
    echo "platform stubs: $(find "$STUB_CLASSES" -name '*.class' | wc -l | tr -d ' ') classes"
fi

# ---------------------------------------------------------------- engine
for d in Utils/src UtilsFPS/src UtilsText/src; do
    if [ ! -d "$d" ] || [ -z "$(ls -A "$d" 2>/dev/null)" ]; then
        echo "submodule content missing: $d -- run 'git submodule update --init'" >&2
        exit 1
    fi
done
rm -rf "$CLASSES" && mkdir -p "$CLASSES"
find src Utils/src UtilsFPS/src UtilsText/src -name '*.java' > build/sources.txt
echo "compiling $(wc -l < build/sources.txt | tr -d ' ') sources"
$JAVAC -nowarn -encoding UTF-8 -source 1.3 -target 1.3 -bootclasspath "$PLATFORM" \
    -d "$CLASSES" "@build/sources.txt"
echo "engine classes: $(find "$CLASSES" -name '*.class' | wc -l | tr -d ' ')"

# ---------------------------------------------------------------- preverify
# Optional: without it the JAR runs nowhere but a desktop JVM test harness.
PACK="$CLASSES"
if [ -n "$PREVERIFY" ] || command -v preverify >/dev/null 2>&1; then
    PV=${PREVERIFY:-preverify}
    rm -rf build/preverified && mkdir -p build/preverified
    echo "preverifying with $PV"
    "$PV" -classpath "$PLATFORM:$CLASSES" -d build/preverified "$CLASSES"
    PACK=build/preverified
    echo "preverified classes: $(find "$PACK" -name '*.class' | wc -l | tr -d ' ')"
else
    echo "preverify: skipped (set PREVERIFY=<path>/preverify for a device build)"
fi

# ---------------------------------------------------------------- jar + jad
mkdir -p "$DIST"
rm -f "$DIST/Quantum.jar" "$DIST/Quantum.jad"
python3 - "$ROOT" "$PACK" <<'PY'
import os, sys, zipfile

root, classes, res, dist = sys.argv[1], sys.argv[2], "res", "dist"
jar, jad = os.path.join(dist, "Quantum.jar"), os.path.join(dist, "Quantum.jad")

# nbproject/project.properties: manifest.midlets + manifest.others
attrs = [
    ("MIDlet-Name", "Quantum"),
    ("MIDlet-Version", "1.0"),
    ("MIDlet-Vendor", "AE-MODS.RU"),
    ("MIDlet-1", "Quantum,/images/icon.png,code.utils.Main"),
    ("MIDlet-Info-URL", "ae-mods.ru"),
    ("Nokia-MIDlet-Category", "Game"),
    ("Nokia-MIDlet-Background-Event", "pause"),
    ("Nokia-UI-Enhancement", "MusicKeysSupported"),
    ("MicroEdition-Configuration", "CLDC-1.1"),
    ("MicroEdition-Profile", "MIDP-2.0"),
]

def manifest(extra):
    lines = []
    for k, v in attrs + extra:
        line = "%s: %s" % (k, v)
        # JAR spec: wrap continuation lines at 72 bytes
        while len(line) > 70:
            lines.append(line[:70])
            line = " " + line[70:]
        lines.append(line)
    return ("\r\n".join(lines) + "\r\n\r\n").encode("utf-8")

entries = []
for base, _, files in os.walk(classes):
    for name in sorted(files):
        p = os.path.join(base, name)
        entries.append((os.path.relpath(p, classes), p))
for base, _, files in os.walk(res):
    for name in sorted(files):
        p = os.path.join(base, name)
        entries.append((os.path.relpath(p, res), p))
entries.sort()

with zipfile.ZipFile(jar, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as z:
    z.writestr("META-INF/MANIFEST.MF", manifest([]))
    for arc, path in entries:
        z.write(path, arc)

size = os.path.getsize(jar)
with open(jad, "w") as f:
    f.write(manifest([("MIDlet-Jar-URL", "Quantum.jar"),
                      ("MIDlet-Jar-Size", str(size))]).decode("utf-8"))

ncls = sum(1 for a, _ in entries if a.endswith(".class"))
print("jar : %s  %d bytes, %d entries (%d classes)" % (jar, size, len(entries), ncls))
print("jad : %s  %d bytes" % (jad, os.path.getsize(jad)))
PY
