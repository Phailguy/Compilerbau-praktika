// TEST_17: Kommentare (Zeilen- und Blockkommentare)
#include "hsbi_runtime.h"

int main() {
    // Zeilenkommentar - sollte ignoriert werden
    int x = 10; // Kommentar am Ende der Zeile

    /* Blockkommentar auf einer Zeile */
    int y = 20;

    /*
       Mehrzeiliger
       Blockkommentar
    */
    int z = 30;

    print_int(x);  // 10
    print_int(y);  // 20
    print_int(z);  // 30

    // Blockkommentar innerhalb von Code
    int a = 1 /* mitten im Ausdruck */ + 2;
    print_int(a);  // 3

    // Verschachtelte Situation (kein echtes Verschachteln, aber tricky)
    int b = 5; /* Kommentar mit // drin */
    print_int(b);  // 5

    // Leerer Blockkommentar
    /**/
    int c = 7;
    print_int(c);  // 7

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
10
20
30
3
5
7
*/
