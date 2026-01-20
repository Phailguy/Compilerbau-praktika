// TEST_21: Vererbungs-Fehler
#include "hsbi_runtime.h"

// === TEST A: Zyklische Vererbung ===
// class A : public B { public: int x; };  // FEHLER: B erbt von A (Zyklus)
// class B : public A { public: int y; };

// === TEST B: Selbst-Vererbung ===
// class C : public C { public: int x; };  // FEHLER: Klasse erbt von sich selbst

// Wenn alle Tests auskommentiert sind:
int main() {
    print_string("No inheritance error test active");
    return 0;
}
/* EXPECT_ERROR (je nach aktivem Test):
A: cyclic inheritance detected / base class not found
B: class cannot inherit from itself / cyclic inheritance
*/
