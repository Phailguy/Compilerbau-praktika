// TEST_06: Funktionen (Definition, Aufruf, Ueberladung)
#include "hsbi_runtime.h"

// Einfache Funktion
int add(int a, int b) {
    return a + b;
}

// Void-Funktion
void greet() {
    print_string("Hello");
}

// Funktion mit mehreren returns
int abs(int x) {
    if (x < 0) {
        return -x;
    }
    return x;
}

// Rekursive Funktion
int fib(int n) {
    if (n <= 1) {
        return n;
    }
    return fib(n - 1) + fib(n - 2);
}

// Ueberladene Funktionen nach Aritaet
int foo(int x) {
    return x + 1;
}

int foo(int x, int y) {
    return x + y;
}

// Ueberladung nach Typ
int process(int x) {
    return x * 2;
}

int process(bool b) {
    if (b) {
        return 100;
    }
    return 0;
}

string process(string s) {
    return s;
}

int main() {
    // Einfache Aufrufe
    print_int(add(3, 5));     // 8
    greet();                   // Hello
    print_int(abs(-7));        // 7
    print_int(abs(7));         // 7

    // Rekursion
    print_int(fib(0));         // 0
    print_int(fib(1));         // 1
    print_int(fib(6));         // 8

    // Ueberladung nach Aritaet
    print_int(foo(5));         // 6
    print_int(foo(3, 4));      // 7

    // Ueberladung nach Typ
    print_int(process(10));    // 20
    print_int(process(true));  // 100
    print_int(process(false)); // 0
    print_string(process("test")); // test

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
8
Hello
7
7
0
1
8
6
7
20
100
0
test
*/
