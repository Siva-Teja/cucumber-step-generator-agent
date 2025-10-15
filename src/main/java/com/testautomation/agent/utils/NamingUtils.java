package com.testautomation.agent.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for naming conventions
 */
public class NamingUtils {
    
    /**
     * Convert string to camelCase
     */
    public String toCamelCase(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        
        String[] words = input.toLowerCase().split("[^a-zA-Z0-9]+");
        StringBuilder result = new StringBuilder(words[0]);
        
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(StringUtils.capitalize(words[i]));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Convert string to PascalCase
     */
    public String toPascalCase(String input) {
        String camelCase = toCamelCase(input);
        return StringUtils.capitalize(camelCase);
    }
    
    /**
     * Convert string to snake_case
     */
    public String toSnakeCase(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        
        return input.toLowerCase()
                   .replaceAll("[^a-zA-Z0-9]+", "_")
                   .replaceAll("^_+|_+$", "");
    }
    
    /**
     * Convert string to kebab-case
     */
    public String toKebabCase(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        
        return input.toLowerCase()
                   .replaceAll("[^a-zA-Z0-9]+", "-")
                   .replaceAll("^-+|-+$", "");
    }
    
    /**
     * Generate method name from step text
     */
    public String generateMethodName(String stepText, String namingConvention) {
        // Clean the step text
        String cleaned = stepText.toLowerCase()
                               .replaceAll("\"[^\"]*\"", "")
                               .replaceAll("\\d+", "")
                               .replaceAll("[^a-zA-Z0-9\\s]", "")
                               .trim();
        
        switch (namingConvention.toUpperCase()) {
            case "CAMEL_CASE":
                return toCamelCase(cleaned);
            case "PASCAL_CASE":
                return toPascalCase(cleaned);
            case "SNAKE_CASE":
                return toSnakeCase(cleaned);
            case "KEBAB_CASE":
                return toKebabCase(cleaned);
            default:
                return toCamelCase(cleaned);
        }
    }
    
    /**
     * Generate class name from step type
     */
    public String generateClassName(String stepType, String namingConvention) {
        String baseName = stepType + "StepDefinitions";
        
        switch (namingConvention.toUpperCase()) {
            case "CAMEL_CASE":
                return toCamelCase(baseName);
            case "PASCAL_CASE":
                return toPascalCase(baseName);
            case "SNAKE_CASE":
                return toSnakeCase(baseName);
            case "KEBAB_CASE":
                return toKebabCase(baseName);
            default:
                return toPascalCase(baseName);
        }
    }
    
    /**
     * Generate package name
     */
    public String generatePackageName(String basePackage, String stepType) {
        return basePackage + "." + stepType.toLowerCase();
    }
    
    /**
     * Generate file name
     */
    public String generateFileName(String className) {
        return className + ".java";
    }
    
    /**
     * Validate method name
     */
    public boolean isValidMethodName(String methodName) {
        if (StringUtils.isBlank(methodName)) {
            return false;
        }
        
        // Check if starts with letter or underscore
        if (!Character.isLetter(methodName.charAt(0)) && methodName.charAt(0) != '_') {
            return false;
        }
        
        // Check if contains only valid characters
        return methodName.matches("[a-zA-Z0-9_]+");
    }
    
    /**
     * Validate class name
     */
    public boolean isValidClassName(String className) {
        if (StringUtils.isBlank(className)) {
            return false;
        }
        
        // Check if starts with letter
        if (!Character.isLetter(className.charAt(0))) {
            return false;
        }
        
        // Check if contains only valid characters
        return className.matches("[a-zA-Z0-9]+");
    }
    
    /**
     * Sanitize string for use in code
     */
    public String sanitizeForCode(String input) {
        if (StringUtils.isBlank(input)) {
            return "";
        }
        
        return input.replaceAll("[^a-zA-Z0-9_]", "")
                   .replaceAll("^[0-9]", "_$0");
    }
}
