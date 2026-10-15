# JavaME API stubs

Compile-time-only stand-ins for the JavaME platform classes the engine is
written against. They let `tools/build-jar.sh` produce `Quantum.jar` on a plain
desktop JDK, without Sun/Oracle's proprietary Wireless Toolkit or Java ME SDK.

**These classes are never shipped.** They are used as `-bootclasspath` while
compiling, so none of their bytecode ends up in the JAR. Method bodies are
empty stubs and return zero/null — they exist so the compiler can resolve
signatures, nothing else.

## Coverage

| API | Package | Source |
|---|---|---|
| CLDC 1.1 | `java.lang`, `java.util`, `java.io` | the subset the 117 engine sources actually reference |
| MIDP 2.0 | `javax.microedition.lcdui`, `.lcdui.game`, `.midlet`, `.rms` | MIDP 2.0 spec |
| JSR-75 | `javax.microedition.io`, `.io.file` | PDA Profile / FileConnection |
| JSR-135 | `javax.microedition.media`, `.media.control`, `.media.protocol` | MMAPI 1.1 |
| JSR-184 | `javax.microedition.m3g` | Mobile 3D Graphics 1.1 |

Which of these the engine touches is visible with:

```sh
grep -rhoE "^import +[a-z][a-zA-Z0-9_.]*;" src Utils/src UtilsFPS/src UtilsText/src \
    | grep -E "javax\.microedition" | sort -u
```

## Known divergence from the published API

`src/code/Rendering/Meshes/M3GParser.java:177` calls

```java
TriangleStripArray iIb = new TriangleStripArray( 0, stripLen );   // stripLen is int[]
```

JSR-184 1.1 documents only `TriangleStripArray(int[], int[])` and
`TriangleStripArray(VertexArray, int[])`, so that call does not match the
published API. A matching `TriangleStripArray(int, int[])` constructor is
declared here so the repository compiles **unmodified** — no engine source was
changed to get the build through. If the real `jsr184_1.1.jar` rejects that
line, it is a latent bug in `M3GParser`, and the fix belongs there (the call
almost certainly wants a `VertexArray` of indices as the first argument).

## What the stubs cannot give you

- **No preverification by itself.** A CLDC device or the WTK emulator refuses
  classes that have not been preverified. `sh tools/build-preverify.sh` builds
  a `preverify` for x86_64 Linux from the phoneME sources; then
  `PREVERIFY=.toolchain/preverify sh tools/build-jar.sh` packages the
  preverified classes into the JAR.
- **No API checking.** A stub is not a spec. A method that exists here but not
  in MIDP 2.0 would compile and then fail at runtime on a device.
- **No behaviour.** Anything run against these stubs does nothing, which is
  exactly why `tools/difftest` links the harness against them only for the
  engine's own integer code, never for the platform calls.

## Getting the real platform jars (and `preverify`)

The stubs stand in for the API; they do not replace it. For a device build you
want the real jars plus the `preverify` tool.

Sun Java Wireless Toolkit 2.5.2_01 — the platform named by
`configs.DefaultColorPhone` in `nbproject/project.properties`. URL and the
cookie are the ones used by the `labac-dev/j2me-env` Docker build:

```sh
wget --no-cookies --no-check-certificate \
     --header "Cookie: oraclelicense=accept-securebackup-cookie" \
     -O wtk-2.5.2_01.bin.sh \
     "https://download.oracle.com/otn-pub/java/sun_java_wireless_toolkit/2.5.2_01/sun_java_wireless_toolkit-2.5.2_01-linuxi486.bin.sh"
```

It installs `lib/cldcapi11.jar`, `lib/midpapi21.jar`, `lib/mmapi.jar`,
`lib/jsr184.jar`, `lib/jsr75.jar`, ... and `bin/preverify`. The JDK it was built
for: `https://repo.huaweicloud.com/java/jdk/6u45-b06/jdk-6u45-linux-i586.bin`.
Or skip the install:

```sh
docker pull ghcr.io/labac-dev/j2me-env:latest
docker run --rm -it -v "$PWD:/workspace" ghcr.io/labac-dev/j2me-env:latest
```

The project's *active* platform is Oracle Java ME SDK 3.4 (`platform.bootclasspath`
lists `midp_2.0.jar`, `cldc_1.1.jar`, `jsr184_1.1.jar`, `jsr75_1.0.jar`,
`jsr234_1.0.jar`, `jsr226_1.0.jar`); that one comes from Oracle's Java ME archive
behind a login. WTK 2.5.2 covers the same APIs at MIDP 2.1.

Build against the real thing:

```sh
WTK=/opt/WTK2.5.2
MIDP_LIBS=$WTK/lib/cldcapi11.jar:$WTK/lib/midpapi21.jar:$WTK/lib/mmapi.jar:$WTK/lib/jsr184.jar:$WTK/lib/jsr75.jar \
PREVERIFY=$WTK/bin/preverify sh tools/build-jar.sh
```

Open-source API sources, if you would rather read the API than download it:
`barteo/microemu` (`api/cldcapi10`, `api/cldcapi11`, `api/midpapi20`),
`magicus/phoneME` (archived Sun phoneME sources), `SquirrelJME/SquirrelJME`.
The microemu modules ship `*-signature.xml` files, which are a usable check on
what a stub set like this one may and may not declare.
