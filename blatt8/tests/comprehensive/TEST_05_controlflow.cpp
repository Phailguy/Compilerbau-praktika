// TEST_05: Kontrollfluss (if/else/while/block)
#include "hsbi_runtime.h"

int main() {
    // if ohne else
    int x = 5;
    if (x > 3) {
        print_int(1);  // 1
    }

    // if mit else
    if (x < 3) {
        print_int(2);
    } else {
        print_int(3);  // 3
    }

    // Verschachtelte if/else
    int y = 10;
    if (y > 15) {
        print_int(4);
    } else {
        if (y > 5) {
            print_int(5);  // 5
        } else {
            print_int(6);
        }
    }

    // while-Schleife
    int i = 0;
    int sum = 0;
    while (i < 5) {
        sum = sum + i;
        i = i + 1;
    }
    print_int(sum);  // 0+1+2+3+4 = 10

    // Verschachtelte while
    int a = 0;
    int count = 0;
    while (a < 3) {
        int b = 0;
        while (b < 2) {
            count = count + 1;
            b = b + 1;
        }
        a = a + 1;
    }
    print_int(count);  // 6

    // Block-Scope
    {
        int z = 99;
        print_int(z);  // 99
    }
    // z ist hier nicht mehr sichtbar

    // Implizite Bool-Konversion in if/while
    if (1) {
        print_int(7);  // 7
    }
    if (0) {
        print_int(8);  // nicht ausgefuehrt
    }
    if ("hello") {
        print_int(9);  // 9
    }
    if ("") {
        print_int(10);  // nicht ausgefuehrt (leerer String = false)
    }

    // while mit impliziter Konversion
    int counter = 3;
    while (counter) {
        print_int(counter);  // 3, 2, 1
        counter = counter - 1;
    }

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
1
3
5
10
6
99
7
9
3
2
1
*/
