// NEG_02: Referenz ohne Initialisierung
#include "hsbi_runtime.h"

int main() {
    int& ref;  // ERROR: Referenz muss initialisiert werden
    return 0;
}
/* EXPECT: Fehler - Referenz ohne Initialisierung */
