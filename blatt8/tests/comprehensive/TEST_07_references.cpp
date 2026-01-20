// TEST_07: C++-Referenzen
#include "hsbi_runtime.h"

void modifyByRef(int& x) {
    x = x * 2;
}

void noModify(int x) {
    x = x * 2;
}

void swap(int& a, int& b) {
    int temp = a;
    a = b;
    b = temp;
}

int main() {
    // Referenz-Variable
    int original = 10;
    int& ref = original;
    print_int(ref);          // 10
    print_int(original);     // 10

    // Zuweisung ueber Referenz aendert Original
    ref = 20;
    print_int(original);     // 20
    print_int(ref);          // 20

    // Zuweisung ans Original aendert auch Referenz-Sicht
    original = 30;
    print_int(ref);          // 30

    // Referenz-Parameter: by-reference modifiziert Original
    int val = 5;
    modifyByRef(val);
    print_int(val);          // 10

    // By-value modifiziert Original NICHT
    int val2 = 5;
    noModify(val2);
    print_int(val2);         // 5

    // Swap mit Referenzen
    int x = 1;
    int y = 2;
    swap(x, y);
    print_int(x);            // 2
    print_int(y);            // 1

    // Referenz auf Referenz-Ziel
    int base = 100;
    int& r1 = base;
    int& r2 = r1;  // r2 referenziert auch base
    r2 = 200;
    print_int(base);         // 200
    print_int(r1);           // 200
    print_int(r2);           // 200

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
10
10
20
20
30
10
5
2
1
200
200
200
*/
