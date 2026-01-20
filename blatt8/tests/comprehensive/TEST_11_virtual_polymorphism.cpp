// TEST_11: Virtual/Polymorphie - Dynamischer vs. Statischer Dispatch
#include "hsbi_runtime.h"

class Base {
public:
    int x;

    Base() {
        x = 1;
    }

    // NICHT virtuell: statischer Dispatch
    int staticMethod() {
        return x;
    }

    // Virtuell: dynamischer Dispatch
    virtual int virtualMethod() {
        return x * 10;
    }
};

class Derived : public Base {
public:
    int y;

    Derived() {
        x = 2;
        y = 3;
    }

    // Ueberschreibt nicht-virtuelle Methode (Hiding)
    int staticMethod() {
        return x + y;
    }

    // Ueberschreibt virtuelle Methode
    int virtualMethod() {
        return x * 100 + y;
    }
};

class GrandChild : public Derived {
public:
    int z;

    GrandChild() {
        z = 4;
    }

    // Erbt Virtual-Eigenschaft von Base::virtualMethod
    int virtualMethod() {
        return z * 1000;
    }
};

int main() {
    // Direkte Aufrufe auf Basisklasse
    Base b;
    print_int(b.staticMethod());    // 1
    print_int(b.virtualMethod());   // 10

    // Direkte Aufrufe auf abgeleitete Klasse
    Derived d;
    print_int(d.staticMethod());    // 5 (2+3)
    print_int(d.virtualMethod());   // 203 (2*100+3)

    // Referenz auf Basistyp, zeigt auf Derived
    Base& ref = d;

    // Statischer Dispatch: nutzt Methode des STATISCHEN Typs (Base)
    print_int(ref.staticMethod());  // 2 (Base::staticMethod, x=2)

    // Dynamischer Dispatch: nutzt Methode des DYNAMISCHEN Typs (Derived)
    print_int(ref.virtualMethod()); // 203 (Derived::virtualMethod)

    // GrandChild: virtuelle Methode vererbt sich
    GrandChild gc;
    gc.x = 5;
    gc.y = 6;
    gc.z = 7;

    // Direkter Aufruf
    print_int(gc.virtualMethod());  // 7000

    // Referenz als Base
    Base& refGc = gc;
    print_int(refGc.virtualMethod()); // 7000 (dynamischer Dispatch bis GrandChild)
    print_int(refGc.staticMethod());  // 5 (statischer Dispatch auf Base)

    // Referenz als Derived
    Derived& refDer = gc;
    print_int(refDer.virtualMethod()); // 7000 (dynamisch)
    print_int(refDer.staticMethod());  // 11 (5+6, statisch Derived)

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
1
10
5
203
2
203
7000
7000
5
7000
11
*/
