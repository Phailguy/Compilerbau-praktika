// NEG_11: Division durch Null (Laufzeitfehler)
#include "hsbi_runtime.h"

int main() {
    int x = 10 / 0;  // RUNTIME ERROR: Division durch Null
    return 0;
}
/* EXPECT: Laufzeitfehler - Division durch Null */
