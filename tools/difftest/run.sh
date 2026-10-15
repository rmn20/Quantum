#!/bin/sh
# Differential test for the renderer: builds a pristine tree (git ref, HEAD by
# default) and the working tree, runs the same deterministic harness against
# both and compares every hash. Any pixel that changed shows up as a diff.
#
#   JAVA_HOME   a JDK (needs javac and java)
#   MIDP_LIBS   ':' separated JavaME SDK jars or class dirs, e.g.
#               $SDK/lib/cldc_1.1.jar:$SDK/lib/midp_2.0.jar:$SDK/lib/mmapi.jar:
#               $SDK/lib/jsr75_1.0.jar:$SDK/lib/jsr184_1.1.jar:$SDK/lib/jsr226_1.0.jar
#   REF         the reference build (default HEAD)
#
# usage: tools/difftest/run.sh [bench]
set -e

cd "$(dirname "$0")/../.."
ROOT=$(pwd)
OUT=${OUT:-/tmp/quantum-difftest}
REF=${REF:-HEAD}
JAVA_HOME=${JAVA_HOME:?JAVA_HOME must point at a JDK}
MIDP_LIBS=${MIDP_LIBS:?MIDP_LIBS must list the JavaME SDK jars}
JAVAC="$JAVA_HOME/bin/javac"
JAVA="$JAVA_HOME/bin/java"

build() { # $1 = out dir, rest = source roots
    out=$1; shift
    rm -rf "$out"; mkdir -p "$out"
    find "$@" -name '*.java' > "$out/sources.txt"
    "$JAVAC" -nowarn -source 1.3 -target 1.3 -bootclasspath "$MIDP_LIBS" \
        -d "$out" "@$out/sources.txt"
}

# ---- reference build: the pristine tree at $REF ---------------------------
rm -rf "$OUT/ref-src"; mkdir -p "$OUT/ref-src"
git archive "$REF" | tar -x -C "$OUT/ref-src"
build "$OUT/ref" "$OUT/ref-src/src" Utils/src UtilsFPS/src UtilsText/src tools/difftest/src

# ---- build under test: the working tree -----------------------------------
build "$OUT/new" src Utils/src UtilsFPS/src UtilsText/src tools/difftest/src

run() {
    "$JAVA" -cp "$1:$MIDP_LIBS" harness.Harness
    "$JAVA" -cp "$1:$MIDP_LIBS" code.Rendering.EngineHarness
}

run "$OUT/ref" > "$OUT/ref.txt"
run "$OUT/new" > "$OUT/new.txt"

echo "sections: $(grep -c '^HASH' "$OUT/ref.txt")"
if diff -u "$OUT/ref.txt" "$OUT/new.txt"; then
    echo "OK: identical output for $(REF=$REF; echo $REF) and the working tree"
else
    echo "DIFFERENT: see the diff above"; exit 1
fi

if [ "$1" = "bench" ]; then
    echo "--- reference ($REF)"
    "$JAVA" -cp "$OUT/ref:$MIDP_LIBS" harness.Bench
    echo "--- working tree"
    "$JAVA" -cp "$OUT/new:$MIDP_LIBS" harness.Bench
fi
