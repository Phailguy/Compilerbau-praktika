#include "RefCounter.h"

RefCounter::RefCounter() : count(1) {}
void RefCounter::inc() { ++count; }
int RefCounter::dec() { return --count; }
int RefCounter::get() const { return count; }
