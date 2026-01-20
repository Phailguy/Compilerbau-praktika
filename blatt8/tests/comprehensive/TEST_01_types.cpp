// TEST_01: Alle primitiven Typen und Literale
#include "hsbi_runtime.h"

int main() {
    // bool
    bool b1 = true;
    bool b2 = false;
    print_bool(b1);  // 1
    print_bool(b2);  // 0

    // int
    int i1 = 0;
    int i2 = 42;
    int i3 = -17;
    print_int(i1);   // 0
    print_int(i2);   // 42
    print_int(i3);   // -17

    // char
    char c1 = 'A';
    char c2 = 'z';
    char c3 = '\0';
    char c4 = '\n';
    print_char(c1);  // A
    print_char(c2);  // z

    // string
    string s1 = "Hello";
    string s2 = "";
    string s3 = "mit \"escape\"";
    print_string(s1);  // Hello
    print_string(s2);  // (leer)
    print_string(s3);  // mit "escape"

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
true
false
0
42
-17
A
z
Hello

mit "escape"
*/
