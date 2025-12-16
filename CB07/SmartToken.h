#ifndef SMARTTOKEN_H
#define SMARTTOKEN_H

#include "Token.h"
#include "RefCounter.h"

class SmartToken {
public:
    SmartToken(Token* p = nullptr);
    SmartToken(const SmartToken& sp);
    ~SmartToken();

    SmartToken& operator=(const SmartToken& sp);

    Token& operator*();
    Token* operator->();
    bool operator==(const SmartToken& sp) const;

    int refCount() const;

private:
    Token* pObj;
    RefCounter* rc;

    void release();
};

#endif
