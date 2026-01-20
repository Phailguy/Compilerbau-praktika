// NEG_12: Zyklische Vererbung
#include "hsbi_runtime.h"

class A : public B {
public:
    int x;
};

class B : public A {  // ERROR: Zyklus A -> B -> A
public:
    int y;
};

int main() {
    return 0;
}
/* EXPECT: Fehler - Vererbungszyklus */
