#ifndef RINGBUFFER_H
#define RINGBUFFER_H

#include "SmartToken.h"

class RingBuffer {
public:
    RingBuffer(unsigned int size);
    ~RingBuffer();

    SmartToken readBuffer();
    void writeBuffer(const SmartToken& data);

private:
    unsigned int count;
    unsigned int head;
    unsigned int size;
    SmartToken* elems;
};

#endif
