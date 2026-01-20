// TEST_04: Logische Operatoren mit Short-Circuit
#include "hsbi_runtime.h"

// Hilfsfunktion die einen Seiteneffekt hat (gibt Wert aus und returned bool)
bool sideEffectTrue() {
    print_string("CALLED");
    return true;
}

bool sideEffectFalse() {
    print_string("CALLED");
    return false;
}

int main() {
    // Grundlegende Logik
    print_bool(true && true);    // true
    print_bool(true && false);   // false
    print_bool(false && true);   // false
    print_bool(false && false);  // false

    print_bool(true || true);    // true
    print_bool(true || false);   // true
    print_bool(false || true);   // true
    print_bool(false || false);  // false

    print_bool(!true);           // false
    print_bool(!false);          // true
    print_bool(!!true);          // true

    // Short-Circuit AND: rechte Seite wird NICHT ausgewertet wenn links false
    print_string("---AND---");
    bool r1 = false && sideEffectTrue();  // sideEffectTrue sollte NICHT aufgerufen werden
    print_string("after1");

    bool r2 = true && sideEffectTrue();   // sideEffectTrue WIRD aufgerufen
    print_string("after2");

    // Short-Circuit OR: rechte Seite wird NICHT ausgewertet wenn links true
    print_string("---OR---");
    bool r3 = true || sideEffectTrue();   // sideEffectTrue sollte NICHT aufgerufen werden
    print_string("after3");

    bool r4 = false || sideEffectTrue();  // sideEffectTrue WIRD aufgerufen
    print_string("after4");

    // Kombinationen
    print_string("---COMBO---");
    print_bool(true && true || false);   // true
    print_bool(false || true && true);   // true
    print_bool(!(true && false));        // true

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
true
false
false
false
true
true
true
false
false
true
true
---AND---
after1
CALLED
after2
---OR---
after3
CALLED
after4
---COMBO---
true
true
true
*/
