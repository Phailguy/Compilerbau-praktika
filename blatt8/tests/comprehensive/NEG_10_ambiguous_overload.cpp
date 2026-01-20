// NEG_10: Mehrdeutige Ueberladung
#include "hsbi_runtime.h"

int foo(int x) {
    return x;
}

int foo(int y) {  // ERROR: Gleiche Signatur wie oben
    return y * 2;
}

int main() {
    int x = foo(5);
    return 0;
}
/* EXPECT: Fehler - mehrdeutige Ueberladung */
