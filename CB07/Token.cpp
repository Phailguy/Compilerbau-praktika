#include "Token.h"
#include <cstring>

Token::Token(const char* l, int r, int c) : row(r), col(c) {
    size_t len = std::strlen(l);
    lexem = new char[len + 1];
    std::strcpy(lexem, l);
}

Token::Token(const Token& other) : row(other.row), col(other.col) {
    size_t len = std::strlen(other.lexem);
    lexem = new char[len + 1];
    std::strcpy(lexem, other.lexem);
}

Token& Token::operator=(const Token& other) {
    if (this != &other) {
        delete[] lexem;
        row = other.row;
        col = other.col;
        size_t len = std::strlen(other.lexem);
        lexem = new char[len + 1];
        std::strcpy(lexem, other.lexem);
    }
    return *this;
}

Token::~Token() {
    delete[] lexem;
}

const char* Token::getLexem() const { return lexem; }
int Token::getRow() const { return row; }
int Token::getCol() const { return col; }
