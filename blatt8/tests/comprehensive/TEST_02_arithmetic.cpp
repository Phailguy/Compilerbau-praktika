// TEST_02: Arithmetische Operatoren
#include "hsbi_runtime.h"

int main() {
    // Addition
    print_int(3 + 5);      // 8
    print_int(0 + 0);      // 0
    print_int(-3 + 5);     // 2

    // Subtraktion
    print_int(10 - 4);     // 6
    print_int(4 - 10);     // -6

    // Multiplikation
    print_int(3 * 4);      // 12
    print_int(0 * 100);    // 0
    print_int(-3 * 4);     // -12

    // Division
    print_int(10 / 3);     // 3
    print_int(15 / 5);     // 3
    print_int(-10 / 3);    // -3

    // Modulo
    print_int(10 % 3);     // 1
    print_int(15 % 5);     // 0
    print_int(7 % 4);      // 3

    // Unaeres Plus und Minus
    int x = 5;
    print_int(+x);         // 5
    print_int(-x);         // -5
    print_int(--x);        // 5 (doppeltes Minus)

    // Kombinationen
    print_int(2 + 3 * 4);     // 14 (Praezedenz!)
    print_int((2 + 3) * 4);   // 20
    print_int(10 - 2 - 3);    // 5 (linksassoziativ)

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
8
0
2
6
-6
12
0
-12
3
3
-3
1
0
3
5
-5
5
14
20
5
*/
