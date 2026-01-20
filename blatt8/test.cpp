// Simple test program

int add(int a, int b) {
    return a + b;
}

int main() {
    int x = 5;
    int y = 10;
    int result = add(x, y);

    print_string("Result: ");
    print_int(result);

    return 0;
}
