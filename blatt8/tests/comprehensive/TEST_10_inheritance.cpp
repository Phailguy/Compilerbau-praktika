// TEST_10: Vererbung
#include "hsbi_runtime.h"

class Animal {
public:
    int age;
    string name;

    Animal() {
        age = 0;
        name = "unknown";
    }

    Animal(int a, string n) {
        age = a;
        name = n;
    }

    int getAge() {
        return age;
    }

    string getName() {
        return name;
    }

    string speak() {
        return "...";
    }
};

class Dog : public Animal {
public:
    string breed;

    Dog() {
        breed = "mixed";
        name = "Buddy";  // Geerbtes Feld
    }

    Dog(string b) {
        breed = b;
    }

    string speak() {
        return "Woof!";
    }

    string getBreed() {
        return breed;
    }
};

class Cat : public Animal {
public:
    bool indoor;

    Cat() {
        indoor = true;
        name = "Whiskers";
    }

    string speak() {
        return "Meow!";
    }
};

int main() {
    // Basisklasse
    Animal a;
    print_string(a.getName());   // unknown
    print_int(a.getAge());       // 0
    print_string(a.speak());     // ...

    // Abgeleitete Klasse erbt Felder und Methoden
    Dog d;
    print_string(d.getName());   // Buddy (geerbte Methode)
    print_int(d.getAge());       // 0 (geerbtes Feld via geerbte Methode)
    print_string(d.speak());     // Woof! (ueberschrieben)
    print_string(d.getBreed());  // mixed (eigene Methode)

    // Basisklassen-Konstruktor wird implizit aufgerufen
    Cat c;
    print_string(c.getName());   // Whiskers
    print_string(c.speak());     // Meow!

    // Geerbte Felder direkt zugreifen
    d.age = 5;
    print_int(d.age);            // 5
    print_int(d.getAge());       // 5

    // Mehrere Ebenen von Feldern
    d.name = "Rex";
    d.breed = "German Shepherd";
    print_string(d.name);        // Rex
    print_string(d.breed);       // German Shepherd

    return 0;
}
/* EXPECT (Zeile fuer Zeile):
unknown
0
...
Buddy
0
Woof!
mixed
Whiskers
Meow!
5
5
Rex
German Shepherd
*/
