// TEST_14: Scoping und Shadowing (ohne globale Variablen)
#include "hsbi_runtime.h"

int getValue() {
    return 42;
}

int main() {
    // Einfaches Block-Scoping
    int x = 1;
    print_int(x);        // 1
    {
        int x = 2;       // Shadows outer x
        print_int(x);    // 2
        {
            int x = 3;
            print_int(x); // 3
        }
        print_int(x);    // 2
    }
    print_int(x);        // 1

    // Shadowing in Schleifen
    int i = 100;
    print_int(i);        // 100

    int sum = 0;
    {
        int i = 0;       // Neuer Scope
        while (i < 3) {
            sum = sum + i;
            i = i + 1;
        }
    }
    print_int(sum);      // 0+1+2 = 3
    print_int(i);        // 100 (urspruengliches i unveraendert)

    // Tiefe Verschachtelung
    int a = 1;
    {
        int b = 2;
        {
            int c = 3;
            print_int(a + b + c);  // 6
        }
        // c nicht mehr sichtbar
        print_int(a + b);          // 3
    }
    // b nicht mehr sichtbar

    // Funktion kann aufgerufen werden
    print_int(getValue());         // 42

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
1
2
3
2
1
100
3
100
6
3
42
*/
