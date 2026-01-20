#!/bin/bash
# Run the C++ interpreter

cd "$(dirname "$0")"

# Detect OS and use appropriate classpath separator
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OS" == "Windows_NT" ]]; then
    # Windows
    java -cp "lib/antlr-4.13.1-complete.jar;out/production" de.hsbi.interpreter.Main "$@"
else
    # Unix/Linux/Mac
    java -cp "lib/antlr-4.13.1-complete.jar:out/production" de.hsbi.interpreter.Main "$@"
fi
