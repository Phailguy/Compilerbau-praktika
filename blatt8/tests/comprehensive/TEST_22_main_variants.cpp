// TEST_22: Verschiedene main()-Varianten
// Dieser Test prueft, ob int main() funktioniert
#include "hsbi_runtime.h"

int main() {
    print_string("int main works");
    return 0;
}
/* EXPECT (Zeile fuer Zeile):
int main works
*/
