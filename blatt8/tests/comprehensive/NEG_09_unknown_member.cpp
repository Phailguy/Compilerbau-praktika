// NEG_09: Zugriff auf unbekanntes Member
#include "hsbi_runtime.h"

class Point {
public:
    int x;
    int y;
};

int main() {
    Point p;
    int z = p.z;  // ERROR: z existiert nicht in Point
    return 0;
}
/* EXPECT: Fehler - unbekanntes Member */
