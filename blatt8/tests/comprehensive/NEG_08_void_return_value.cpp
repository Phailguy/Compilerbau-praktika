// NEG_08: Void-Funktion gibt Wert zurueck
#include "hsbi_runtime.h"

void foo() {
    return 5;  // ERROR: void-Funktion darf keinen Wert zurueckgeben
}

int main() {
    foo();
    return 0;
}
/* EXPECT: Fehler - void-Funktion mit Rueckgabewert */
