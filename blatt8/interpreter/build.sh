#!/bin/bash
# Build script for C++ Interpreter - creates a Fat JAR

set -e  # Exit on error

cd "$(dirname "$0")"

echo "=== C++ Interpreter Build ==="
echo

# Find jar command (might not be in PATH on Windows)
if command -v jar &> /dev/null; then
    JAR_CMD="jar"
elif [ -f "/c/Program Files/Java/jdk-21/bin/jar.exe" ]; then
    JAR_CMD="/c/Program Files/Java/jdk-21/bin/jar.exe"
elif [ -n "$JAVA_HOME" ] && [ -f "$JAVA_HOME/bin/jar" ]; then
    JAR_CMD="$JAVA_HOME/bin/jar"
else
    echo "Error: 'jar' command not found. Please ensure JDK is installed."
    exit 1
fi

# Directories
SRC_DIR="src/main/java"
OUT_DIR="out/production"
LIB_DIR="lib"
ANTLR_JAR="$LIB_DIR/antlr-4.13.1-complete.jar"
FAT_JAR="interpreter.jar"

# Check if ANTLR JAR exists
if [ ! -f "$ANTLR_JAR" ]; then
    echo "Error: ANTLR JAR not found at $ANTLR_JAR"
    exit 1
fi

# Clean and create output directory
echo "[1/4] Cleaning output directory..."
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Compile all Java files
echo "[2/4] Compiling Java sources..."
# Detect OS for classpath separator
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OS" == "Windows_NT" ]]; then
    SEP=";"
else
    SEP=":"
fi

find "$SRC_DIR" -name "*.java" | grep -v "/parser/src/" > /tmp/sources.txt
javac -encoding UTF-8 -cp "${ANTLR_JAR}${SEP}${SRC_DIR}" -d "$OUT_DIR" @/tmp/sources.txt
rm /tmp/sources.txt

# Extract ANTLR runtime classes (only what we need)
echo "[3/4] Extracting ANTLR runtime classes..."
SCRIPT_DIR="$(pwd)"
cd "$OUT_DIR"
"$JAR_CMD" xf "$SCRIPT_DIR/$ANTLR_JAR" org/antlr/v4/runtime
cd "$SCRIPT_DIR"

# Create Fat JAR with manifest
echo "[4/4] Creating Fat JAR..."
echo "Main-Class: de.hsbi.interpreter.Main" > /tmp/manifest.txt
"$JAR_CMD" cfm "$FAT_JAR" /tmp/manifest.txt -C "$OUT_DIR" .
rm /tmp/manifest.txt

# Show result
echo
echo "=== Build complete! ==="
echo "Created: $FAT_JAR ($(du -h "$FAT_JAR" | cut -f1))"
echo
echo "Usage:"
echo "  java -jar $FAT_JAR              # REPL mode"
echo "  java -jar $FAT_JAR program.cpp  # Run file"
