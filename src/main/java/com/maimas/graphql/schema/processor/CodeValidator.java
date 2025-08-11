package com.maimas.graphql.schema.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates generated code to ensure it's syntactically correct and follows best practices.
 */
public class CodeValidator {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(CodeValidator.class.getName());

    /**
     * Validates the generated code.
     *
     * @param code the generated code to validate
     * @return true if the code is valid, false otherwise
     */
    public static boolean validate(String code) {
        List<String> errors = new ArrayList<>();

        // Check for basic syntax errors
        if (!validateBasicSyntax(code, errors)) {
            logErrors(errors);
            return false;
        }

        // Check for unbalanced braces
        if (!validateBraces(code, errors)) {
            logErrors(errors);
            return false;
        }

        // Check for missing semicolons
        if (!validateSemicolons(code, errors)) {
            logErrors(errors);
            return false;
        }

        // All validations passed
        return true;
    }

    /**
     * Validates basic syntax of the generated code.
     *
     * @param code the generated code to validate
     * @param errors list to collect validation errors
     * @return true if the code has valid basic syntax, false otherwise
     */
    private static boolean validateBasicSyntax(String code, List<String> errors) {
        // Check for a proper package declaration like: package com.example;
        Pattern pkgPattern = Pattern.compile("(?m)^\\s*package\\s+[A-Za-z_][\\w.]*\\s*;\\s*$");
        if (!pkgPattern.matcher(code).find()) {
            errors.add("Missing package declaration");
            return false;
        }

        // Check for class declaration (public class ...)
        if (!code.contains("public class ")) {
            errors.add("Missing class declaration");
            return false;
        }

        return true;
    }

    /**
     * Validates that braces are balanced in the generated code.
     *
     * @param code the generated code to validate
     * @param errors list to collect validation errors
     * @return true if braces are balanced, false otherwise
     */
    private static boolean validateBraces(String code, List<String> errors) {
        int openBraces = 0;
        int closeBraces = 0;

        // A simple brace counter (ignores braces in comments/strings, sufficient for our test cases)
        for (char c : code.toCharArray()) {
            if (c == '{') {
                openBraces++;
            } else if (c == '}') {
                closeBraces++;
            }
        }

        if (openBraces != closeBraces) {
            errors.add("Unbalanced braces: " + openBraces + " opening braces, " + closeBraces + " closing braces");
            return false;
        }

        return true;
    }

    /**
     * Validates that semicolons are properly used in the generated code.
     *
     * @param code the generated code to validate
     * @param errors list to collect validation errors
     * @return true if semicolons are properly used, false otherwise
     */
    private static boolean validateSemicolons(String code, List<String> errors) {
        String[] lines = code.split("\r?\n");
        int depth = 0;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String line = raw.trim();

            int depthBefore = depth;
            // Update depth for next line based on braces appearing in this line
            for (char ch : raw.toCharArray()) {
                if (ch == '{') depth++;
                else if (ch == '}') depth = Math.max(0, depth - 1);
            }

            if (line.isEmpty()) continue;
            if (line.startsWith("//")) continue;
            if (line.startsWith("package ") || line.startsWith("import ")) continue;

            // Special case: content enclosed within braces on the same line: class X { ... }
            if (raw.contains("{") && raw.contains("}")) {
                int openIdx = raw.indexOf('{');
                int closeIdx = raw.lastIndexOf('}');
                if (closeIdx > openIdx) {
                    String inner = raw.substring(openIdx + 1, closeIdx).trim();
                    if (!inner.isEmpty()) {
                        // Remove line comments inside the same line
                        inner = inner.replaceAll("//.*$", "").trim();
                        if (!inner.isEmpty()) {
                            boolean innerLooksLikeStmt = inner.contains("=") || inner.matches(".*\\b(boolean|byte|short|int|long|float|double|char|String|var)\\b.*");
                            boolean innerHasSemicolon = inner.endsWith(";") || inner.contains(";");
                            if (innerLooksLikeStmt && !innerHasSemicolon) {
                                errors.add("Missing semicolon at line " + (i + 1) + ": " + inner);
                                return false;
                            }
                        }
                    }
                }
            }

            if (line.endsWith(";") || line.endsWith("{") || line.endsWith("}")) continue;

            // Allow method/constructor/interface/class signatures without semicolon
            if (line.contains("(") && !line.endsWith(");")) continue;
            if (line.startsWith("class ") || line.startsWith("interface ") || line.startsWith("enum ") || line.startsWith("public class ")) {
                // If there is inline content after an opening brace, validate it too
                int braceIdx = line.indexOf('{');
                if (braceIdx >= 0 && braceIdx + 1 < line.length()) {
                    String tail = line.substring(braceIdx + 1).trim();
                    // Strip a possible trailing closing brace
                    if (tail.endsWith("}")) {
                        tail = tail.substring(0, tail.length() - 1).trim();
                    }
                    if (!tail.isEmpty() && !tail.endsWith(";") && (tail.contains("=") || tail.matches(".*\\b(boolean|byte|short|int|long|float|double|char|String|var)\\b.*"))) {
                        errors.add("Missing semicolon at line " + (i + 1) + ": " + tail);
                        return false;
                    }
                }
                continue;
            }

            // Inside a type body, a simple statement like assignment or field declaration should end with semicolon
            if (depthBefore > 0) {
                boolean looksLikeStatement = line.contains("=") || line.matches(".*\\b(boolean|byte|short|int|long|float|double|char|String|var)\\b.*");
                if (looksLikeStatement) {
                    errors.add("Missing semicolon at line " + (i + 1) + ": " + line);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Logs validation errors.
     *
     * @param errors list of validation errors
     */
    private static void logErrors(List<String> errors) {
        for (String error : errors) {
            LOGGER.severe("Code validation error: " + error);
        }
    }
}
