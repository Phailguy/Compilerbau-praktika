// TEST_08: Klassen Grundlagen
#include "hsbi_runtime.h"

class Point {
public:
    int x;
    int y;

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int sum() {
        return x + y;
    }

    void setXY(int newX, int newY) {
        x = newX;
        y = newY;
    }
};

class Counter {
public:
    int value;

    void increment() {
        value = value + 1;
    }

    void add(int n) {
        value = value + n;
    }

    int get() {
        return value;
    }
};

int main() {
    // Objekt erstellen (Default-Konstruktor)
    Point p;
    print_int(p.x);      // 0 (Default-Initialisierung)
    print_int(p.y);      // 0

    // Felder setzen
    p.x = 3;
    p.y = 4;
    print_int(p.x);      // 3
    print_int(p.y);      // 4

    // Methoden aufrufen
    print_int(p.getX()); // 3
    print_int(p.getY()); // 4
    print_int(p.sum());  // 7

    // Methode die Felder aendert
    p.setXY(10, 20);
    print_int(p.sum());  // 30

    // Zweites Objekt
    Point q;
    q.x = 100;
    q.y = 200;
    print_int(q.sum());  // 300

    // Counter-Beispiel
    Counter c;
    print_int(c.get());  // 0
    c.increment();
    print_int(c.get());  // 1
    c.add(5);
    print_int(c.get());  // 6

    // Objektzuweisung (Wertkopie)
    Point copy = p;
    print_int(copy.x);   // 10
    print_int(copy.y);   // 20

    // Aenderung am Original beeinflusst Kopie NICHT
    p.x = 999;
    print_int(copy.x);   // 10 (unveraendert)

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
0
0
3
4
3
4
7
30
300
0
1
6
10
20
10
*/
