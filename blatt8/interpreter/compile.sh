#!/bin/bash
# Compile the C++ interpreter

cd "$(dirname "$0")"

echo "Compiling interpreter..."
mkdir -p out/production

javac -encoding UTF-8 \
  -cp "lib/antlr-4.13.1-complete.jar:src/main/java" \
  -d out/production \
  $(find src/main/java/de/hsbi/interpreter -name "*.java" | grep -v "/parser/src/")

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run REPL mode:"
    echo "  ./run.sh"
    echo ""
    echo "To run with a file:"
    echo "  ./run.sh examples/test.cpp"
else
    echo "Compilation failed!"
    exit 1
fi
