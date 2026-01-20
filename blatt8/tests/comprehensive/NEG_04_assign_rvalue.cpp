// NEG_04: Zuweisung an RValue
#include "hsbi_runtime.h"

int main() {
    5 = 10;  // ERROR: Kann nicht an RValue zuweisen
    return 0;
}
/* EXPECT: Fehler - LValue erforderlich */
