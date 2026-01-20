// NEG_01: Typfehler - verschiedene Typen bei binaeren Operatoren
#include "hsbi_runtime.h"

int main() {
    int x = 5 + "hello";  // ERROR: int + string
    return 0;
}
/* EXPECT: Typfehler / Type error */
