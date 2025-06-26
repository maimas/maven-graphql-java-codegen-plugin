package com.maimas.graphql.schema.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates generated code to ensure it's syntactically correct and follows best practices.
 */
public class CodeValidator {

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
        // Check for package declaration
        if (!code.contains("package ")) {
            errors.add("Missing package declaration");
            return false;
        }

        // Check for class declaration
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
        // Check for statements without semicolons
        Pattern pattern = Pattern.compile("(\\w+\\s*=\\s*[^;{]+?$)|([^;{}]$)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            errors.add("Missing semicolon at: " + matcher.group());
            return false;
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
            System.err.println("Code validation error: " + error);
        }
    }
}
