// TEST_12: Object Slicing
#include "hsbi_runtime.h"

class Base {
public:
    int x;

    Base() {
        x = 1;
    }

    virtual int getValue() {
        return x;
    }
};

class Derived : public Base {
public:
    int y;

    Derived() {
        x = 10;
        y = 20;
    }

    int getValue() {
        return x + y;
    }
};

void takeByValue(Base b) {
    // b ist eine Kopie (gesliced!)
    print_int(b.getValue());  // Ruft Base::getValue auf!
}

void takeByRef(Base& b) {
    // b ist eine Referenz (kein Slicing)
    print_int(b.getValue());  // Dynamischer Dispatch
}

int main() {
    Derived d;
    d.x = 5;
    d.y = 7;

    // Direkter Aufruf
    print_int(d.getValue());     // 12 (5+7)

    // Zuweisung an Basistyp = Slicing
    Base sliced = d;
    print_int(sliced.getValue()); // 5 (nur Base-Teil, Base::getValue)

    // Sliced-Objekt hat nur x, kein y
    print_int(sliced.x);          // 5

    // Aenderung am Original beeinflusst geslicede Kopie NICHT
    d.x = 100;
    print_int(sliced.x);          // 5 (unveraendert)

    // By-Value Parameteruebergabe = Slicing
    Derived d2;
    d2.x = 3;
    d2.y = 4;
    takeByValue(d2);              // 3 (gesliced zu Base)

    // By-Reference = kein Slicing
    takeByRef(d2);                // 7 (dynamischer Dispatch zu Derived)

    // Mehrfaches Slicing
    Derived d3;
    d3.x = 50;
    d3.y = 60;
    Base b1 = d3;                 // Slicing
    Base b2 = b1;                 // Kopie von Base
    print_int(b2.getValue());     // 50

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
12
5
5
5
3
7
50
*/
