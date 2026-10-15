package harness;

import java.util.Random;

/**
 * Proves the mask/mo dulo substitution used in Sprite: for a power of two
 * length L, (v & 0x7FFFFFFF) % L  ==  v & (L-1)  for every int v, for both the
 * signed (>>12) and unsigned (>>>12) variants.
 */
public class MaskProof {
    public static void main(String[] args) {
        int bad = 0;
        int[] lens = new int[]{2, 4, 8, 16, 64, 256, 1024, 4096, 65536, 1 << 20};
        Random r = new Random(31337L);
        long checked = 0;
        for (int li = 0; li < lens.length; li++) {
            int L = lens[li];
            int mask = L - 1;
            // exhaustive over the reachable u2>>12 range plus interesting edges
            for (int v = -300000; v <= 300000; v++) {
                int a = (v & Integer.MAX_VALUE) % L;
                int b = v & mask;
                if (a != b) { bad++; if (bad < 5) System.out.println("MISMATCH signed v=" + v + " L=" + L + " " + a + " " + b); }
                checked++;
            }
            for (int i = 0; i < 300000; i++) {
                int v = r.nextInt();
                int a = (v & Integer.MAX_VALUE) % L;
                int b = v & mask;
                if (a != b) { bad++; if (bad < 5) System.out.println("MISMATCH rand v=" + v + " L=" + L + " " + a + " " + b); }
                int ua = v >>> 12;
                int a2 = (ua & Integer.MAX_VALUE) % L;
                int b2 = ua & mask;
                if (a2 != b2) { bad++; if (bad < 5) System.out.println("MISMATCH unsigned v=" + v + " L=" + L); }
                checked += 2;
            }
            int[] edges = new int[]{0, 1, L - 1, L, L + 1, -1, -L, Integer.MIN_VALUE, Integer.MAX_VALUE,
                                    Integer.MIN_VALUE >> 12, Integer.MAX_VALUE >> 12, -4096, 4095, 4096};
            for (int i = 0; i < edges.length; i++) {
                int v = edges[i];
                if ((v & Integer.MAX_VALUE) % L != (v & mask)) { bad++; System.out.println("MISMATCH edge " + v + " L=" + L); }
                if (((v >>> 12) & Integer.MAX_VALUE) % L != ((v >>> 12) & mask)) { bad++; System.out.println("MISMATCH edge u " + v + " L=" + L); }
                checked += 2;
            }
        }
        System.out.println("MASKPROOF checked=" + checked + " mismatches=" + bad);

        // and confirm a non power of two really does need the modulo
        int L = 10000;
        int v = 12345678;
        System.out.println("NONPOW2 modulo=" + ((v & Integer.MAX_VALUE) % L) + " mask=" + (v & (L - 1)));
    }
}
