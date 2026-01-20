// TEST_19: Semantische Fehler
// Jeder dieser Testfaelle sollte einzeln getestet werden (einer nach dem anderen auskommentieren)
#include "hsbi_runtime.h"

// === TEST A: Mehrfachdefinition im selben Scope ===
// int testDouble() {
//     int x = 5;
//     int x = 10;  // FEHLER: x schon definiert
//     return x;
// }

// === TEST B: Variable vor Definition verwendet ===
// int testUndefined() {
//     int y = x;  // FEHLER: x nicht definiert
//     int x = 5;
//     return y;
// }

// === TEST C: Zuweisung an Nicht-LValue ===
// int testNotLValue() {
//     int x = 5;
//     (x + 1) = 10;  // FEHLER: (x + 1) ist kein LValue
//     return x;
// }

// === TEST D: Zuweisung an Literal ===
// int testLiteralAssign() {
//     5 = 10;  // FEHLER: 5 ist kein LValue
//     return 0;
// }

// Wenn alle Tests auskommentiert sind, sollte dieser Code laufen:
int main() {
    print_string("No semantic error test active");
    return 0;
}
/* EXPECT_ERROR (je nach aktivem Test):
A: symbol 'x' already defined
B: undefined symbol 'x'
C: left side of assignment must be an lvalue
D: left side of assignment must be an lvalue
*/
