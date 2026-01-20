// TEST_22c: Programm ohne main() - sollte trotzdem geladen werden (fuer REPL)
#include "hsbi_runtime.h"

int add(int a, int b) {
    return a + b;
}

class Counter {
public:
    int value;

    Counter() {
        value = 0;
    }

    void increment() {
        value = value + 1;
    }
};

// Kein main() - Programm definiert nur Funktionen und Klassen
// In REPL koennte man dann add(1,2) aufrufen oder Counter c; c.increment(); etc.

/* EXPECT:
(keine Ausgabe, da kein main())
*/
