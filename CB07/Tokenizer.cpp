#include <sstream>
#include <vector>
#include <string>
#include "Token.h"

void tokenize(const std::string& input, std::vector<Token>& tokens) {
    std::istringstream iss(input);
    std::string word;
    int col = 0;

    while (iss >> word) {
        tokens.emplace_back(word.c_str(), 1, col);
        col += word.length() + 1;
    }
}
