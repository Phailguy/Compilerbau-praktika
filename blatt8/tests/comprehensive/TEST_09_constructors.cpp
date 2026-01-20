// TEST_09: Konstruktoren
#include "hsbi_runtime.h"

class Simple {
public:
    int a;
    bool b;
    char c;
    string s;
};

class WithDefault {
public:
    int x;
    int y;

    WithDefault() {
        x = 10;
        y = 20;
    }
};

class WithParams {
public:
    int x;
    int y;

    WithParams() {
        x = 0;
        y = 0;
    }

    WithParams(int a) {
        x = a;
        y = a;
    }

    WithParams(int a, int b) {
        x = a;
        y = b;
    }

    int sum() {
        return x + y;
    }
};

class Container {
public:
    WithDefault inner;

    int getInnerX() {
        return inner.x;
    }
};

int main() {
    // Synthetisierter Default-Konstruktor: Default-Werte
    Simple s;
    print_int(s.a);        // 0
    print_bool(s.b);       // 0 (false)
    // print_char bei '\0' ist schwierig zu testen

    // Expliziter Default-Konstruktor
    WithDefault wd;
    print_int(wd.x);       // 10
    print_int(wd.y);       // 20

    // Parametrisierte Konstruktoren
    WithParams p0;
    print_int(p0.sum());   // 0

    WithParams p1 = WithParams(5);
    print_int(p1.sum());   // 10

    WithParams p2 = WithParams(3, 7);
    print_int(p2.sum());   // 10

    // Konstruktor-Ueberladung
    WithParams p3 = WithParams(100, 200);
    print_int(p3.x);       // 100
    print_int(p3.y);       // 200

    // Klassen als Felder: Default-Konstruktor wird aufgerufen
    Container cont;
    print_int(cont.getInnerX());  // 10

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
0
0
10
20
0
10
10
100
200
10
*/
