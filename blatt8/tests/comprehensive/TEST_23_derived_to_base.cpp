// TEST_23: Derived-to-Base Zuweisung (ergaenzt TEST_12)
#include "hsbi_runtime.h"

class Animal {
public:
    int legs;

    Animal() {
        legs = 0;
    }

    int getLegs() {
        return legs;
    }
};

class Dog : public Animal {
public:
    int tailLength;

    Dog() {
        legs = 4;
        tailLength = 20;
    }
};

class Cat : public Animal {
public:
    bool hasWhiskers;

    Cat() {
        legs = 4;
        hasWhiskers = true;
    }
};

int main() {
    Dog d;
    Cat c;

    // Derived zu Base Zuweisung (Slicing)
    Animal a1 = d;  // Dog -> Animal
    Animal a2 = c;  // Cat -> Animal

    print_int(a1.getLegs());  // 4
    print_int(a2.getLegs());  // 4

    // Zuweisung an existierende Base-Variable
    Animal a3;
    a3 = d;
    print_int(a3.getLegs());  // 4

    // Base-Referenz auf Derived (kein Slicing)
    Animal& ref = d;
    print_int(ref.getLegs());  // 4

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
4
4
4
4
*/
