// TEST_13: Zuweisung als Ausdruck
#include "hsbi_runtime.h"

int main() {
    // Zuweisung hat einen Wert
    int x;
    int y = (x = 5);
    print_int(x);        // 5
    print_int(y);        // 5

    // Verkettete Zuweisung (rechtsassoziativ)
    int a;
    int b;
    int c;
    a = b = c = 10;
    print_int(a);        // 10
    print_int(b);        // 10
    print_int(c);        // 10

    // Zuweisung in Ausdruecken
    int d = 0;
    if ((d = 7) > 5) {
        print_int(d);    // 7
    }

    // Zuweisung in while-Bedingung
    int counter = 3;
    int result;
    while ((result = counter) > 0) {
        print_int(result);  // 3, 2, 1
        counter = counter - 1;
    }

    // Komplexe verkettete Zuweisung
    int p;
    int q;
    int r;
    p = (q = (r = 100) + 1) + 1;
    print_int(r);        // 100
    print_int(q);        // 101
    print_int(p);        // 102

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
5
5
10
10
10
7
3
2
1
100
101
102
*/
