// TEST_18: Fehlerbehandlung - Division durch 0
// Dieser Test sollte einen Laufzeitfehler erzeugen
#include "hsbi_runtime.h"

int main() {
    int x = 10;
    int y = 0;
    int z = x / y;  // Division durch 0!
    print_int(z);
    return 0;
}
/* EXPECT_ERROR:
division by zero
*/
