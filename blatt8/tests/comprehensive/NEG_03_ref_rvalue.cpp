// NEG_03: Referenz mit RValue initialisieren
#include "hsbi_runtime.h"

int main() {
    int& ref = 5;  // ERROR: Referenz kann nur mit LValue initialisiert werden
    return 0;
}
/* EXPECT: Fehler - Referenz braucht LValue */
