// TEST_15: Namensaufloesung in Methoden
// Reihenfolge: lokal/Parameter > eigene Members > geerbte Members > global
#include "hsbi_runtime.h"

class Base {
public:
    int value;

    Base() {
        value = 100;
    }

    int getValue() {
        return value;
    }
};

class Derived : public Base {
public:
    int ownValue;

    Derived() {
        ownValue = 200;
        value = 150;  // Geerbtes Feld
    }

    // Parameter shadowed Member
    int testParameter(int value) {
        return value;  // Parameter, nicht Member!
    }

    // Lokale Variable shadowed Member
    int testLocal() {
        int ownValue = 999;
        return ownValue;  // Lokale Variable, nicht Member!
    }

    // Zugriff auf geerbtes Feld
    int testInherited() {
        return value;  // Geerbtes Feld von Base
    }

    // Zugriff auf eigenes Feld
    int testOwn() {
        return ownValue;  // Eigenes Feld
    }

    // Methoden-Ueberladung
    int process(int x) {
        return x + 1;
    }

    int process(int x, int y) {
        return x + y;
    }
};

int helper() {
    return 42;
}

int main() {
    Derived d;

    // Parameter shadows Member
    print_int(d.testParameter(777));  // 777 (Parameter)

    // Lokale Variable shadows Member
    print_int(d.testLocal());         // 999 (lokal)

    // Zugriff auf geerbtes Feld
    print_int(d.testInherited());     // 150

    // Zugriff auf eigenes Feld
    print_int(d.testOwn());           // 200

    // Geerbte Methode
    print_int(d.getValue());          // 150

    // Ueberladene Methoden
    print_int(d.process(5));          // 6
    print_int(d.process(3, 4));       // 7

    // Globale Funktion ist aufrufbar
    print_int(helper());              // 42

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
777
999
150
200
150
6
7
42
*/
