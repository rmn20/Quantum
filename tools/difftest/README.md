# Differential test for the renderer

The engine is a software rasterizer, so "the optimization did not change the
picture" can be checked mechanically: render the same deterministic scene with
two builds of the engine and compare the framebuffer.

`run.sh` does exactly that. It builds a pristine tree (`git archive $REF`,
`HEAD` by default) and the working tree, runs the same harness against both and
diffs the output. The harness prints one FNV-1a hash per stage, so a difference
points at the stage that changed instead of just saying "something differs".

```sh
JAVA_HOME=/path/to/jdk \
MIDP_LIBS=$SDK/lib/cldc_1.1.jar:$SDK/lib/midp_2.0.jar:$SDK/lib/mmapi.jar:$SDK/lib/jsr75_1.0.jar:$SDK/lib/jsr184_1.1.jar:$SDK/lib/jsr226_1.0.jar \
tools/difftest/run.sh bench
```

`MIDP_LIBS` is the bootclasspath: the JavaME SDK jars from the platform the
project is built against (`platform.bootclasspath` in
`nbproject/project.properties`). `bench` additionally prints the timings of both
builds.

Everything here lives outside `src/`, so the NetBeans/Ant build ignores it and
none of it ends up in the MIDlet.

## What is covered

| file | covers |
| --- | --- |
| `src/harness/Harness.java` | `TexturingAffine`/`TexturingPers` for every drawmode (0-13), mip-mapped and flat textures, `Main.persQ` 0-4 and `Main.fogQ` 0-2, `Polygon3V`/`Polygon4V`/`Sprite` rendering, non power-of-two textures, `DirectX7` transform/project/drawLine, and `RenderObjectBuffer.sort` (random, reverse sorted and all-equal keys) |
| `src/code/Rendering/EngineHarness.java` | `RayCast.rayCast`/`superFastRayCast` over a mesh with two materials, and `HeightComputer` |
| `src/harness/Bench.java` | timings: rasterizer passes, sprite blits, buffer growth/sort, vertex transform |
| `src/harness/MaskProof.java` | brute force proof that `(v & 0x7FFFFFFF) % L == v & (L-1)` for power-of-two `L` (the substitution used in `Sprite`) |

The harnesses write no files and touch no device API: they construct
`RawImage`s from generated pixel arrays, so they run on a desktop JVM.

## Sensitivity

A check that cannot fail proves nothing, so both harnesses are worth breaking on
purpose once: changing `sz2 = 255 - sz2` to `254 - sz2` in
`Polygon4V.szCalcfogAdd()` changes 5 of the 107 hashes, and reading
`materials[0]` instead of `materials[mat]` in `RayCast` changes all 4 raycast
hashes.
