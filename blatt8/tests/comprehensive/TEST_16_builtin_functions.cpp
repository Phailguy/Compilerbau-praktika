// TEST_16: Eingebaute Funktionen (print_*)
#include "hsbi_runtime.h"

int main() {
    // print_bool
    print_bool(true);      // 1
    print_bool(false);     // 0
    print_bool(5 > 3);     // 1
    print_bool(5 < 3);     // 0

    // print_int
    print_int(0);          // 0
    print_int(42);         // 42
    print_int(-17);        // -17
    print_int(3 + 4 * 5);  // 23

    // print_char
    print_char('A');       // A
    print_char('z');       // z
    print_char('5');       // 5
    print_char(' ');       // (Leerzeichen)

    // print_string
    print_string("Hello"); // Hello
    print_string("World"); // World
    print_string("");      // (leer)
    print_string("123");   // 123

    // Variablen an print_*
    bool b = true;
    int i = 99;
    char c = 'X';
    string s = "test";
    print_bool(b);         // 1
    print_int(i);          // 99
    print_char(c);         // X
    print_string(s);       // test

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
1
0
1
0
0
42
-17
23
A
z
5

Hello
World

123
1
99
X
test
*/
