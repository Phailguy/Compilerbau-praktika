#ifndef REFCOUNTER_H
#define REFCOUNTER_H

class RefCounter {
public:
    RefCounter();
    void inc();
    int dec();
    int get() const;

private:
    int count;
};

#endif
