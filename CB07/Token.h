#ifndef TOKEN_H
#define TOKEN_H

class Token {
public:
    Token(const char* l, int r, int c);
    Token(const Token& other);
    Token& operator=(const Token& other);
    ~Token();

    const char* getLexem() const;
    int getRow() const;
    int getCol() const;

private:
    char* lexem;
    int row;
    int col;
};

#endif
