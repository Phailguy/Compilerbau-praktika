// NEG_06: Mehrfachdeklaration im selben Scope
#include "hsbi_runtime.h"

int main() {
    int x = 5;
    int x = 10;  // ERROR: x bereits deklariert
    return 0;
}
/* EXPECT: Fehler - Mehrfachdeklaration */
