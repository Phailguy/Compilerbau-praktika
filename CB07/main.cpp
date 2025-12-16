#include <iostream>
#include <vector>
#include "RingBuffer.h"
#include "RefCounter.h"
#include "Token.h"
#include "Tokenizer.h"
#include "SmartToken.h"

int main() {
    //Token Test
    Token t("hello", 1, 5);
    std::cout << t.getLexem() << " (" << t.getRow() << "," << t.getCol() << ")\n";

    //Tokenizer Test
    std::vector<Token> tokens;
    tokenize("Das ist ein Test", tokens);

    for (const auto& t : tokens)
        std::cout << t.getLexem() << std::endl;

    //RefCounter Test
    RefCounter rc;
    rc.inc();
    std::cout << "RefCount Test: " << rc.get() << std::endl;
    rc.dec();
    std::cout << "RefCount Test: " << rc.get() << std::endl;

    //Smarttoken Test

    SmartToken s1(new Token("abc", 1, 1));
    SmartToken s2 = s1;

    std::cout << "Smarttoken RefCount: " << s1.refCount() << std::endl; // 2
    SmartToken s3 = s1;
    std::cout << "Smarttoken RefCount: " << s1.refCount() << std::endl;

    //Ringbuffer Test

    RingBuffer rb(2);

    SmartToken t1(new Token("one", 1, 1));
    SmartToken t2(new Token("two", 1, 5));
    SmartToken t3(new Token("three", 1, 9));

    rb.writeBuffer(t1);
    rb.writeBuffer(t2);
    rb.writeBuffer(t3);

    SmartToken r = rb.readBuffer();
    std::cout << r->getLexem() << std::endl;
    r = rb.readBuffer();
    std::cout << r->getLexem() << std::endl;
    r = rb.readBuffer();
    std::cout << r->getLexem() << std::endl;

    return 0;
}
