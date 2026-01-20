// TEST_20: Referenz-Fehler
// Jeder dieser Testfaelle sollte einzeln getestet werden
#include "hsbi_runtime.h"

// === TEST A: Referenz ohne Initialisierung ===
// int testRefNoInit() {
//     int& r;  // FEHLER: Referenz muss initialisiert werden
//     return 0;
// }

// === TEST B: Referenz mit Nicht-LValue initialisieren ===
// int testRefWithRValue() {
//     int& r = 5;  // FEHLER: Referenz braucht LValue
//     return 0;
// }

// === TEST C: Referenz mit Ausdruck initialisieren ===
// int testRefWithExpr() {
//     int x = 5;
//     int& r = x + 1;  // FEHLER: (x + 1) ist kein LValue
//     return 0;
// }

// === TEST D: Referenz als Klassenfeld ===
// class BadClass {
// public:
//     int& ref;  // FEHLER: Referenzen als Felder nicht erlaubt
// };

// === TEST E: Referenz als Rueckgabetyp ===
// int& badReturn() {  // FEHLER: Referenz als Rueckgabetyp nicht erlaubt
//     int x = 5;
//     return x;
// }

// Wenn alle Tests auskommentiert sind:
int main() {
    print_string("No reference error test active");
    return 0;
}
/* EXPECT_ERROR (je nach aktivem Test):
A: reference variable must be initialized
B: reference must be initialized with lvalue
C: reference must be initialized with lvalue
D: reference fields not allowed
E: reference return type not allowed
*/
