package com.maimas.graphql.schema.processor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        return validate(code, null);
    }

    /**
     * Validates the generated code and optionally writes validation errors to a file.
     *
     * @param code the generated code to validate
     * @param errorOutputFile optional path to write validation errors to
     * @return true if the code is valid, false otherwise
     */
    public static boolean validate(String code, String errorOutputFile) {
        // Backward-compatible path: no ignores, default language "Java"
        return validate(code, errorOutputFile, null, "Java");
    }

    /**
     * Validates the generated code with additional options.
     *
     * @param code the generated code to validate
     * @param errorOutputFile optional path to write validation errors to
     * @param ignoredRuleIds optional list of rule IDs to ignore
     * @param languageName optional language name to apply language-specific rules (currently informational)
     * @return true if the code is valid, false otherwise
     */
    public static boolean validate(String code, String errorOutputFile, String[] ignoredRuleIds, String languageName) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        java.util.function.Predicate<String> isIgnored = id -> {
            if (ignoredRuleIds == null || ignoredRuleIds.length == 0) return false;
            for (String s : ignoredRuleIds) {
                if (s != null && s.trim().equalsIgnoreCase(id)) return true;
            }
            return false;
        };

        // BASIC_SYNTAX (ERROR)
        if (!validateBasicSyntax(code, errors)) {
            if (isIgnored.test("BASIC_SYNTAX")) {
                warnings.add("[BASIC_SYNTAX] validation failed but was ignored by configuration");
            } else {
                logErrors(errors, errorOutputFile);
                return false;
            }
        }

        // BRACES_BALANCED (ERROR)
        errors.clear();
        if (!validateBraces(code, errors)) {
            if (isIgnored.test("BRACES_BALANCED")) {
                warnings.add("[BRACES_BALANCED] validation failed but was ignored by configuration");
            } else {
                logErrors(errors, errorOutputFile);
                return false;
            }
        }

        // SEMICOLON_TERMINATION (ERROR)
        errors.clear();
        if (!validateSemicolons(code, errors)) {
            if (isIgnored.test("SEMICOLON_TERMINATION")) {
                warnings.add("[SEMICOLON_TERMINATION] validation failed but was ignored by configuration");
            } else {
                logErrors(errors, errorOutputFile);
                return false;
            }
        }

        // PARENTHESES_BALANCED (WARNING): does not fail, only warns
        String parenWarning = validateParenthesesWarning(code);
        if (parenWarning != null && !isIgnored.test("PARENTHESES_BALANCED")) {
            warnings.add("[PARENTHESES_BALANCED] " + parenWarning);
        }

        // Emit warnings to log (do not fail)
        for (String w : warnings) {
            LOGGER.warning(w);
        }

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
        boolean inBlockComment = false;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i];
            String line = raw.trim();

            // Handle block comments start/end
            if (inBlockComment) {
                if (line.contains("*/")) {
                    inBlockComment = false;
                }
                continue; // ignore any content inside block comments
            }
            if (line.startsWith("/*") || line.startsWith("/**")) {
                inBlockComment = !line.contains("*/");
                continue;
            }
            if (line.startsWith("* ") || line.equals("*")) { // javadoc middle lines
                continue;
            }

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
                        // Remove inline // comments and block /* */ comments present on same line
                        inner = inner.replaceAll("//.*$", "");
                        inner = inner.replaceAll("/\\*.*?\\*/", "");
                        inner = inner.trim();
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
                    // Remove possible inline comments in tail
                    tail = tail.replaceAll("//.*$", "");
                    tail = tail.replaceAll("/\\*.*?\\*/", "").trim();
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
                // Allow continuation lines for multi-line assignments/concatenations
                boolean continuation = line.endsWith("+") || line.endsWith("=") || line.endsWith("&&") || line.endsWith("||") || line.endsWith(":") || line.endsWith("?");
                if (looksLikeStatement && !continuation) {
                    errors.add("Missing semicolon at line " + (i + 1) + ": " + line);
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks for balanced parentheses and returns a warning message if unbalanced; null otherwise.
     */
    private static String validateParenthesesWarning(String code) {
        int open = 0, close = 0;
        for (char c : code.toCharArray()) {
            if (c == '(') open++;
            else if (c == ')') close++;
        }
        if (open != close) {
            return "Unbalanced parentheses: " + open + " opening, " + close + " closing";
        }
        return null;
    }

    /**
     * Logs validation errors and optionally writes them to a file.
     *
     * @param errors list of validation errors
     * @param errorOutputFile optional path to write validation errors to
     */
    private static void logErrors(List<String> errors, String errorOutputFile) {
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("Code Validation Errors:\n");
        errorReport.append("======================\n\n");

        for (String error : errors) {
            String formattedError = "Code validation error: " + error;
            LOGGER.severe(formattedError);
            errorReport.append(formattedError).append("\n");
        }

        // Add suggestions for common errors
        errorReport.append("\nSuggestions for fixing common errors:\n");
        errorReport.append("- Missing package declaration: Ensure your template includes a proper package declaration\n");
        errorReport.append("- Unbalanced braces: Check for missing closing braces in template conditional blocks\n");
        errorReport.append("- Missing semicolons: Add semicolons to field declarations and assignments in templates\n");

        // Write to file if specified
        if (errorOutputFile != null && !errorOutputFile.isEmpty()) {
            try {
                Path outputPath = Paths.get(errorOutputFile);
                Files.createDirectories(outputPath.getParent());
                Files.write(
                    outputPath, 
                    errorReport.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING
                );
                LOGGER.info("Validation errors written to file: " + errorOutputFile);
            } catch (IOException e) {
                LOGGER.severe("Failed to write validation errors to file: " + e.getMessage());
            }
        }
    }

    /**
     * Logs validation errors.
     *
     * @param errors list of validation errors
     */
    private static void logErrors(List<String> errors) {
        logErrors(errors, null);
    }
}
