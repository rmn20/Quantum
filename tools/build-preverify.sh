#!/bin/sh
# Build a working `preverify` for x86_64 Linux from the phoneME sources.
#
#   sh tools/build-preverify.sh [outdir]     # default: .toolchain/preverify
#
# The Sun Wireless Toolkit ships preverify as a binary, but only for i386, and
# this sandbox has no 32-bit loader. The phoneME Feature tree carries the same
# tool in C, so it is built here instead.
#
# Three 64-bit portability fixes are applied to the 2007 sources; each one is
# a place where the code assumes `long` and pointers are 32 bits:
#
#   1. classloader.c get4bytes(): (ptr[0] << 24) overflows a 32-bit int and
#      sign-extends into the 64-bit `unsigned long` result, so the class file
#      magic read as 0xFFFFFFFFCAFEBABE and every class failed with
#      "Bad magic number".
#   2. oobj.h UCALIGN(): casts the pointer to `int`, truncating it to 32 bits
#      on x86_64 - this is what made the verifier segfault on the two largest
#      classes (TexturingAffine, TexturingPers) at check_code.c:1259.
#   3. check_code.c: `long *lpc` / `long *lptr` walk the switch tables of a
#      class file, whose entries are 4 bytes; with an 8-byte `long` the stride
#      is wrong and every tableswitch reported "Illegal branch".
#
# Needs: gcc, curl. Sources come from github.com/magicus/phoneME.
set -e

OUT=${1:-.toolchain/preverify}
WORK=${2:-.toolchain/phoneme-preverifier}
API=https://api.github.com/repos/magicus/phoneME/contents/preverifier/src

mkdir -p "$(dirname "$OUT")" "$WORK/src"
cd "$WORK/src"

# exact listing of preverifier/src in magicus/phoneME (README.TXT excluded).
# opcodes.* are not headers: check_code.c #includes them to build opcode_length.
FILES="check_class.c check_code.c check_code.h classloader.c classresolver.c \
convert_md.c convert_md.h file.c inlinejsr.c jar.c jar.h jar_support.c \
jarint.h jartables.h locale_md.h main.c oobj.h opcodes.h opcodes.in_out \
opcodes.init opcodes.length path.h path_md.h signature.h stubs.c sys_api.h \
sys_support.c sysmacros_md.h tree.h typecodes.h typedefs.h typedefs_md.h \
utf.c utf.h util.c"

for f in $FILES; do
    if [ ! -s "$f" ]; then
        echo "fetching $f"
        curl -sSL "$API/$f" | python3 -c "
import base64, json, sys
d = json.load(sys.stdin)
if 'content' not in d:
    sys.exit('missing in the phoneME tree: $f')
sys.stdout.buffer.write(base64.b64decode(d['content']))" > "$f"
    fi
done

# ---- 64-bit fixes ---------------------------------------------------------
python3 - <<'PY'
import re

p = "oobj.h"; s = open(p).read()
s = s.replace("#define UCALIGN(n) ((unsigned char *)ALIGN((int)(n)))",
              "#define UCALIGN(n) ((unsigned char *)ALIGN((unsigned long)(n)))")
open(p, "w").write(s)

p = "classloader.c"; s = open(p).read()
s = s.replace("unsigned long value = (ptr[0] << 24) + (ptr[1] << 16) +\n"
              "                                 (ptr[2] << 8) + ptr[3];",
              "unsigned long value = ((unsigned long)ptr[0] << 24) + ((unsigned long)ptr[1] << 16) +\n"
              "                                 ((unsigned long)ptr[2] << 8) + (unsigned long)ptr[3];")
open(p, "w").write(s)

p = "check_code.c"; s = open(p).read()
s = s.replace("long *lpc = (long *) UCALIGN(code + offset + 1);\n    long *lptr;",
              "int32_t *lpc = (int32_t *) UCALIGN(code + offset + 1);\n    int32_t *lptr;")
s = s.replace("long *lpc = (long *) UCALIGN(iptr + 1);",
              "int32_t *lpc = (int32_t *) UCALIGN(iptr + 1);")
open(p, "w").write(s)
print("64-bit fixes applied")
PY

# ---- build ----------------------------------------------------------------
# -fcommon:  the 1998-era sources use tentative definitions in headers
# -fno-strict-aliasing: the verifier reads class bytes through int32_t*
gcc -O2 -w -fcommon -fno-strict-aliasing -I. -DUNIX -DLINUX -DJAVAVERIFY -DTRIMMED \
    -o "$OLDPWD/$OUT" \
    check_class.c main.c utf.c check_code.c convert_md.c util.c jar.c \
    jar_support.c classloader.c file.c classresolver.c stubs.c inlinejsr.c \
    sys_support.c

cd "$OLDPWD"
echo "built: $OUT"
"$OUT" 2>&1 | head -2
