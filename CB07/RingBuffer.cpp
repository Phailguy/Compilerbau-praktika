#include "RingBuffer.h"

RingBuffer::RingBuffer(unsigned int s)
    : count(0), head(0), size(s) {
    elems = new SmartToken[size];
}

RingBuffer::~RingBuffer() {
    delete[] elems;
}

SmartToken RingBuffer::readBuffer() {
    if (count == 0)
        return SmartToken();

    SmartToken result = elems[head];
    head = (head + 1) % size;
    --count;
    return result;
}

void RingBuffer::writeBuffer(const SmartToken& data) {
    unsigned int tail = (head + count) % size;

    if (count == size) {
        head = (head + 1) % size;
        --count;
    }

    elems[tail] = data;
    ++count;
}
