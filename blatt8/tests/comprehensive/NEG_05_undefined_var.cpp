// NEG_05: Verwendung undefinierter Variable
#include "hsbi_runtime.h"

int main() {
    int x = y;  // ERROR: y ist nicht definiert (define-before-use)
    return 0;
}
/* EXPECT: Fehler - Variable nicht definiert */
