package de.hsbi.interpreter;

import de.hsbi.interpreter.ast.*;
import de.hsbi.interpreter.parser.*;
import de.hsbi.interpreter.runtime.RuntimeError;
import de.hsbi.interpreter.semantic.SemanticAnalyzer;
import de.hsbi.interpreter.symbols.SymbolTable;
import de.hsbi.interpreter.symbols.SymbolTableBuilder;
import org.antlr.v4.runtime.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * main entry point for the C++ interpreter
 */
public class Main {
    private static SymbolTable symbolTable;
    private static Interpreter interpreter;

    public static void main(String[] args) {
        System.out.println("C++ Interpreter");
        System.out.println("===============");
        System.out.println();

        try {
            if (args.length > 0) {
                // load file
                String filename = args[0];
                System.out.println("Loading file: " + filename);
                System.out.println();

                String source = readFile(filename);
                Program program = parseProgram(source);

                if (program == null) {
                    System.err.println("Failed to parse program");
                    System.exit(1);
                }

                // build symbol table
                SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
                symbolTable = symbolTableBuilder.build(program);

                // check for symbol table building errors
                if (symbolTableBuilder.hasErrors()) {
                    System.err.println("Semantic errors:");
                    for (String error : symbolTableBuilder.getErrors()) {
                        System.err.println("  " + error);
                    }
                    System.exit(1);
                }

                // semantic analysis
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);
                boolean success = semanticAnalyzer.analyze(program);

                if (!success) {
                    System.err.println("Semantic errors:");
                    for (String error : semanticAnalyzer.getErrors()) {
                        System.err.println("  " + error);
                    }
                    System.exit(1);
                }

                // interpret
                interpreter = new Interpreter(symbolTable);
                interpreter.execute(program);

                System.out.println();
                System.out.println("File execution completed.");
                System.out.println();
            } else {
                // no file - start with empty environment
                symbolTable = new SymbolTable();
                interpreter = new Interpreter(symbolTable);
            }

            // start REPL
            startREPL();

        } catch (RuntimeError e) {
            System.err.println("Runtime error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return preprocessSource(content.toString());
    }

    /**
     * Simple preprocessor: removes #include directives and block comments
     */
    private static String preprocessSource(String source) {
        // Remove #include lines
        source = source.replaceAll("(?m)^\\s*#include\\s+[<\"][^>\"]*[>\"]\\s*$", "");
        // Remove /* EXPECT ... */ comments (test expectations)
        source = source.replaceAll("/\\*\\s*EXPECT[^*]*\\*+(?:[^/*][^*]*\\*+)*/", "");
        return source;
    }

    private static Program parseProgram(String source) {
        try {
            // create lexer
            CharStream input = CharStreams.fromString(source);
            CPPLexer lexer = new CPPLexer(input);

            // create parser
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CPPParser parser = new CPPParser(tokens);

            // disable default error listeners
            parser.removeErrorListeners();
            lexer.removeErrorListeners();

            // add custom error listener
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine, String msg, RecognitionException e) {
                    System.err.println("Syntax error at line " + line + ":" + charPositionInLine + " - " + msg);
                }
            });

            // parse
            CPPParser.ProgramContext programContext = parser.program();

            // check for errors
            if (parser.getNumberOfSyntaxErrors() > 0) {
                return null;
            }

            // build AST
            ASTBuilder astBuilder = new ASTBuilder();
            ASTNode ast = astBuilder.visitProgram(programContext);

            return (Program) ast;

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void startREPL() {
        System.out.println("REPL Mode");
        System.out.println("=========");
        System.out.println("Enter C++ statements. Type 'exit' or 'quit' to exit.");
        System.out.println();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder multiLineInput = new StringBuilder();

        while (true) {
            try {
                // print prompt
                if (multiLineInput.length() == 0) {
                    System.out.print(">>> ");
                } else {
                    System.out.print("... ");
                }
                System.out.flush();

                String line = reader.readLine();

                if (line == null) {
                    break; // EOF
                }

                line = line.trim();

                // check for exit
                if (line.equals("exit") || line.equals("quit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                // check for empty line
                if (line.isEmpty() && multiLineInput.length() == 0) {
                    continue;
                }

                // add to multi-line input
                multiLineInput.append(line).append("\n");

                // check if input is complete
                if (isInputComplete(multiLineInput.toString())) {
                    String input = multiLineInput.toString();
                    multiLineInput.setLength(0); // clear buffer

                    // execute input
                    executeREPLInput(input);
                }

            } catch (IOException e) {
                System.err.println("IO error: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                multiLineInput.setLength(0); // clear buffer on error
            }
        }
    }

    private static boolean isInputComplete(String input) {
        // simple heuristic: check if braces are balanced
        int braceCount = 0;
        boolean inString = false;
        boolean inChar = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char next = (i + 1 < input.length()) ? input.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
                continue;
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false;
                    i++; // skip next char
                }
                continue;
            }

            if (inString) {
                if (c == '\\') {
                    i++; // skip next char
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (inChar) {
                if (c == '\\') {
                    i++; // skip next char
                } else if (c == '\'') {
                    inChar = false;
                }
                continue;
            }

            // check for comments
            if (c == '/' && next == '/') {
                inLineComment = true;
                i++; // skip next char
                continue;
            }

            if (c == '/' && next == '*') {
                inBlockComment = true;
                i++; // skip next char
                continue;
            }

            // check for strings/chars
            if (c == '"') {
                inString = true;
                continue;
            }

            if (c == '\'') {
                inChar = true;
                continue;
            }

            // count braces
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
        }

        // input is complete if braces are balanced and ends with ; or }
        if (braceCount != 0) {
            return false;
        }

        String trimmed = input.trim();
        return trimmed.endsWith(";") || trimmed.endsWith("}");
    }

    private static void executeREPLInput(String input) {
        try {
            // check if input is a function or class definition
            String trimmed = input.trim();
            boolean isDefinition = trimmed.matches("(?s)^(class|struct)\\s+\\w+.*") ||
                                  trimmed.matches("(?s)^\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{.*");

            String sourceToParseCode;
            if (isDefinition) {
                // don't wrap definitions
                sourceToParseCode = input;
            } else {
                // wrap input in dummy function for statements
                sourceToParseCode = "void __repl_dummy__() { " + input + " }";
            }

            // parse
            CharStream charStream = CharStreams.fromString(sourceToParseCode);
            CPPLexer lexer = new CPPLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CPPParser parser = new CPPParser(tokens);

            parser.removeErrorListeners();
            lexer.removeErrorListeners();

            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                        int line, int charPositionInLine, String msg, RecognitionException e) {
                    System.err.println("Syntax error: " + msg);
                }
            });

            CPPParser.ProgramContext programContext = parser.program();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                return;
            }

            // build AST
            ASTBuilder astBuilder = new ASTBuilder();
            Program program = (Program) astBuilder.visitProgram(programContext);

            if (program == null) {
                System.err.println("Failed to parse input");
                return;
            }

            // build symbol table incrementally (extends existing symbolTable)
            SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(symbolTable);
            symbolTableBuilder.build(program);

            // immediately remove dummy function so it can be redefined next time
            // (must happen before any return, so errors don't leave it registered)
            if (!isDefinition) {
                symbolTable.getGlobalScope().remove("__repl_dummy__");
            }

            if (symbolTableBuilder.hasErrors()) {
                for (String error : symbolTableBuilder.getErrors()) {
                    System.err.println("Error: " + error);
                }
                return;
            }

            // semantic analysis
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);
            boolean success = semanticAnalyzer.analyze(program);

            if (!success) {
                for (String error : semanticAnalyzer.getErrors()) {
                    System.err.println("Error: " + error);
                }
                return;
            }

            if (isDefinition) {
                // register functions/classes from the definition
                interpreter.execute(program);
                System.out.println("Defined.");
            } else {
                // execute statements from dummy function
                if (program.getFunctions().isEmpty()) {
                    System.err.println("Failed to parse input");
                    return;
                }

                FunctionDecl dummyFunc = program.getFunctions().get(0);
                BlockStmt block = dummyFunc.getBody();

                // check if single expression statement
                boolean isExprStmt = (block.getStatements().size() == 1 &&
                                      block.getStatements().get(0) instanceof ExprStmt);

                // execute statements
                for (Statement stmt : block.getStatements()) {
                    // auto-print for expression statements (evaluate before executing the statement)
                    if (isExprStmt && stmt instanceof ExprStmt) {
                        ExprStmt exprStmt = (ExprStmt) stmt;
                        de.hsbi.interpreter.runtime.Value value = exprStmt.getExpression().accept(interpreter);

                        if (value != null && value.getType().getBaseType() != Type.BaseType.VOID) {
                            printValue(value);
                        }
                    } else {
                        stmt.accept(interpreter);
                    }
                }
            }

        } catch (RuntimeError e) {
            System.err.println("Runtime error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printValue(de.hsbi.interpreter.runtime.Value value) {
        switch (value.getType().getBaseType()) {
            case INT:
                System.out.println(value.getIntValue());
                break;
            case BOOL:
                System.out.println(value.getBoolValue());
                break;
            case CHAR:
                System.out.println("'" + value.getCharValue() + "'");
                break;
            case STRING:
                System.out.println("\"" + value.getStringValue() + "\"");
                break;
            case CLASS:
                System.out.println(value.getObjectValue());
                break;
            default:
                System.out.println(value);
                break;
        }
    }
}
