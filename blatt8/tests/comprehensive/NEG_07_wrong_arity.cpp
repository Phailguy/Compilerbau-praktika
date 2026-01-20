// NEG_07: Falsche Anzahl Argumente bei Funktionsaufruf
#include "hsbi_runtime.h"

int add(int a, int b) {
    return a + b;
}

int main() {
    int x = add(5);  // ERROR: Falsche Anzahl Argumente
    return 0;
}
/* EXPECT: Fehler - Argumentanzahl stimmt nicht */
