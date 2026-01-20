// TEST_03: Vergleichsoperatoren
#include "hsbi_runtime.h"

int main() {
    // Gleichheit fuer alle Typen
    print_bool(5 == 5);           // 1
    print_bool(5 == 6);           // 0
    print_bool(true == true);     // 1
    print_bool(true == false);    // 0
    print_bool('a' == 'a');       // 1
    print_bool('a' == 'b');       // 0
    print_bool("foo" == "foo");   // 1
    print_bool("foo" == "bar");   // 0

    // Ungleichheit fuer alle Typen
    print_bool(5 != 6);           // 1
    print_bool(5 != 5);           // 0
    print_bool(true != false);    // 1
    print_bool('a' != 'b');       // 1
    print_bool("foo" != "bar");   // 1

    // Relationale Operatoren (int)
    print_bool(3 < 5);            // 1
    print_bool(5 < 5);            // 0
    print_bool(5 < 3);            // 0
    print_bool(3 <= 5);           // 1
    print_bool(5 <= 5);           // 1
    print_bool(6 <= 5);           // 0
    print_bool(5 > 3);            // 1
    print_bool(5 > 5);            // 0
    print_bool(5 >= 3);           // 1
    print_bool(5 >= 5);           // 1
    print_bool(5 >= 6);           // 0

    // Relationale Operatoren (char)
    print_bool('a' < 'b');        // 1
    print_bool('b' < 'a');        // 0
    print_bool('a' <= 'a');       // 1
    print_bool('b' > 'a');        // 1
    print_bool('a' >= 'a');       // 1

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
true
false
true
false
true
false
true
false
true
false
true
true
true
true
false
false
true
true
false
true
false
true
true
false
true
false
true
true
true
*/
