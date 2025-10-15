package com.testautomation.agent.generator;

import com.testautomation.agent.model.FeatureStep;
import com.testautomation.agent.model.GenerationConfig;
import com.testautomation.agent.model.StepDefinition;
import com.testautomation.agent.utils.CodeTemplateManager;
import com.testautomation.agent.utils.NamingUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates step definition methods from feature steps
 */
public class StepDefinitionGenerator {
    private static final Logger logger = LoggerFactory.getLogger(StepDefinitionGenerator.class);
    
    private final CodeTemplateManager templateManager;
    private final NamingUtils namingUtils;
    
    public StepDefinitionGenerator() {
        this.templateManager = new CodeTemplateManager();
        this.namingUtils = new NamingUtils();
    }
    
    /**
     * Generate step definitions for all steps
     */
    public Map<String, List<StepDefinition>> generateStepDefinitions(
            Map<String, List<FeatureStep>> allSteps, 
            GenerationConfig config) throws IOException {
        
        Map<String, List<StepDefinition>> generatedDefinitions = new HashMap<>();
        
        // Get unique steps to avoid duplicates
        Set<FeatureStep> uniqueSteps = getUniqueSteps(allSteps);
        
        // Group steps by type for organization
        Map<String, List<FeatureStep>> groupedSteps = groupStepsByType(uniqueSteps);
        
        for (Map.Entry<String, List<FeatureStep>> entry : groupedSteps.entrySet()) {
            String stepType = entry.getKey();
            List<FeatureStep> steps = entry.getValue();
            
            if (shouldGenerateForStepType(stepType, config)) {
                List<StepDefinition> definitions = generateStepDefinitionsForType(
                    stepType, steps, config);
                generatedDefinitions.put(stepType, definitions);
            }
        }
        
        return generatedDefinitions;
    }
    
    /**
     * Generate step definitions for a specific step type
     */
    private List<StepDefinition> generateStepDefinitionsForType(
            String stepType, 
            List<FeatureStep> steps, 
            GenerationConfig config) throws IOException {
        
        List<StepDefinition> definitions = new ArrayList<>();
        Set<String> usedMethodNames = new HashSet<>();
        
        for (FeatureStep step : steps) {
            StepDefinition definition = generateStepDefinition(step, config);
            
            // Ensure unique method names to avoid compilation errors
            String originalMethodName = definition.getMethodName();
            String uniqueMethodName = ensureUniqueMethodName(originalMethodName, usedMethodNames);
            if (!originalMethodName.equals(uniqueMethodName)) {
                definition.setMethodName(uniqueMethodName);
                logger.debug("Renamed method from '{}' to '{}' to avoid duplicates", 
                    originalMethodName, uniqueMethodName);
            }
            
            definitions.add(definition);
            usedMethodNames.add(uniqueMethodName);
        }
        
        return definitions;
    }
    
    /**
     * Ensure method name is unique by appending numbers if needed
     */
    private String ensureUniqueMethodName(String methodName, Set<String> usedNames) {
        if (!usedNames.contains(methodName)) {
            return methodName;
        }
        
        int counter = 1;
        String uniqueName;
        do {
            uniqueName = methodName + counter;
            counter++;
        } while (usedNames.contains(uniqueName));
        
        return uniqueName;
    }
    
    /**
     * Generate a single step definition
     */
    private StepDefinition generateStepDefinition(FeatureStep step, GenerationConfig config) {
        String methodName = generateMethodName(step);
        String annotation = generateAnnotation(step);
        String methodSignature = generateMethodSignature(step, config);
        String methodBody = generateMethodBody(step, config);
        String className = generateClassName(step.getStepType(), config);
        String packageName = generatePackageName(step.getStepType(), config);
        
        StepDefinition definition = new StepDefinition(
            methodName, annotation, methodSignature, methodBody, 
            className, packageName, step.getParameters(), 
            step.getStepType(), step.getText()
        );
        
        definition.setFilePath(generateFilePath(definition, config));
        
        return definition;
    }
    
    /**
     * Generate method name from step text
     */
    private String generateMethodName(FeatureStep step) {
        String text = step.getText().toLowerCase();
        
        // Remove parameters and special characters
        text = text.replaceAll("\"[^\"]*\"", "")
                  .replaceAll("\\d+", "")
                  .replaceAll("[^a-zA-Z0-9\\s]", "")
                  .trim();
        
        // Convert to camelCase
        String[] words = text.split("\\s+");
        StringBuilder methodName = new StringBuilder(words[0]);
        
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                methodName.append(Character.toUpperCase(words[i].charAt(0)))
                         .append(words[i].substring(1));
            }
        }
        
        return methodName.toString();
    }
    
    /**
     * Generate Cucumber annotation
     */
    private String generateAnnotation(FeatureStep step) {
        String keyword = step.getKeyword();
        String text = step.getText();
        
        // Convert parameters to regex patterns
        String regexText = convertToRegex(text);
        
        return String.format("@%s(\"%s\")", keyword, regexText);
    }
    
    /**
     * Convert step text to regex pattern
     */
    private String convertToRegex(String text) {
        // Replace quoted strings with regex groups
        text = text.replaceAll("\"([^\"]+)\"", "\"([^\"]+)\"");
        
        // Replace numbers with regex groups
        text = text.replaceAll("\\b(\\d+)\\b", "(\\\\d+)");
        
        // Replace data table references
        text = text.replaceAll("data table", "datatable");
        
        // Replace doc string references
        text = text.replaceAll("doc string|multiline", "docstring");
        
        // Escape special regex characters that are not part of our patterns
        text = text.replaceAll("([\\[\\](){}.*+?^$|\\\\])", "\\\\$1");
        
        // Restore our intentional patterns
        text = text.replaceAll("\\\\\\(\\\\\\[\\\\\\^\\\\\"\\\\\\]\\\\\\+\\\\\\)", "([^\"]+)");
        text = text.replaceAll("\\\\\\(\\\\\\\\d\\\\\\+\\)", "(\\\\d+)");
        
        return text;
    }
    
    /**
     * Generate method signature
     */
    private String generateMethodSignature(FeatureStep step, GenerationConfig config) {
        StringBuilder signature = new StringBuilder();
        signature.append("public void ").append(generateMethodName(step)).append("(");
        
        List<String> parameters = step.getParameters();
        List<String> methodParams = new ArrayList<>();
        
        for (int i = 0; i < parameters.size(); i++) {
            String paramType = determineParameterType(parameters.get(i), step.getText());
            String paramName = "param" + (i + 1);
            methodParams.add(paramType + " " + paramName);
        }
        
        // Add DataTable parameter if step contains data table
        if (step.getText().toLowerCase().contains("data table")) {
            methodParams.add("DataTable dataTable");
        }
        
        // Add String parameter if step contains doc string
        if (step.getText().toLowerCase().contains("doc string") || 
            step.getText().toLowerCase().contains("multiline")) {
            methodParams.add("String docString");
        }
        
        signature.append(String.join(", ", methodParams));
        signature.append(")");
        
        return signature.toString();
    }
    
    /**
     * Determine parameter type based on content
     */
    private String determineParameterType(String parameter, String stepText) {
        if (parameter.matches("\\d+")) {
            return "int";
        } else if (parameter.equals("DataTable")) {
            return "DataTable";
        } else if (parameter.equals("String")) {
            return "String";
        } else {
            return "String";
        }
    }
    
    /**
     * Generate method body
     */
    private String generateMethodBody(FeatureStep step, GenerationConfig config) {
        String template = templateManager.getTemplate(step.getStepType());
        
        Map<String, String> variables = new HashMap<>();
        variables.put("methodName", generateMethodName(step));
        variables.put("stepText", step.getText());
        variables.put("stepType", step.getStepType());
        variables.put("parameters", String.join(", ", step.getParameters()));
        
        // Generate proper logger statement
        StringBuilder loggerStatement = new StringBuilder();
        loggerStatement.append("logger.info(\"Executing ").append(step.getStepType().toLowerCase())
                      .append(" step: {}\", \"").append(step.getText()).append("\");");
        
        variables.put("loggerStatement", loggerStatement.toString());
        
        return templateManager.processTemplate(template, variables);
    }
    
    /**
     * Generate class name
     */
    private String generateClassName(String stepType, GenerationConfig config) {
        return stepType + "StepDefinitions";
    }
    
    /**
     * Generate package name
     */
    private String generatePackageName(String stepType, GenerationConfig config) {
        return config.getBasePackage() + "." + stepType.toLowerCase();
    }
    
    /**
     * Generate file path
     */
    private String generateFilePath(StepDefinition definition, GenerationConfig config) {
        String packagePath = definition.getPackageName().replace(".", "/");
        return config.getOutputDirectory() + "/" + packagePath + "/" + definition.getClassName() + ".java";
    }
    
    /**
     * Write step definitions to files
     */
    public void writeStepDefinitions(Map<String, List<StepDefinition>> definitions, 
                                   GenerationConfig config) throws IOException {
        
        for (Map.Entry<String, List<StepDefinition>> entry : definitions.entrySet()) {
            String stepType = entry.getKey();
            List<StepDefinition> stepDefinitions = entry.getValue();
            
            // Group definitions by class
            Map<String, List<StepDefinition>> classGroups = stepDefinitions.stream()
                .collect(Collectors.groupingBy(StepDefinition::getClassName));
            
            for (Map.Entry<String, List<StepDefinition>> classEntry : classGroups.entrySet()) {
                String className = classEntry.getKey();
                List<StepDefinition> classDefinitions = classEntry.getValue();
                
                String fileContent = generateClassFile(classDefinitions, config);
                String filePath = classDefinitions.get(0).getFilePath();
                
                // Create directory if it doesn't exist
                File file = new File(filePath);
                FileUtils.forceMkdirParent(file);
                
                // Write file
                FileUtils.writeStringToFile(file, fileContent, "UTF-8");
                logger.info("Generated step definition file: {}", filePath);
            }
        }
    }
    
    /**
     * Generate complete class file content
     */
    private String generateClassFile(List<StepDefinition> definitions, GenerationConfig config) {
        if (definitions.isEmpty()) {
            return "";
        }
        
        StepDefinition firstDef = definitions.get(0);
        String packageName = firstDef.getPackageName();
        String className = firstDef.getClassName();
        String stepType = firstDef.getStepType();
        
        StringBuilder classContent = new StringBuilder();
        
        // Package declaration
        classContent.append("package ").append(packageName).append(";\n\n");
        
        // Imports
        classContent.append(generateImports(stepType));
        
        // Class declaration
        classContent.append("public class ").append(className).append(" {\n\n");
        
        // Add fields based on step type
        classContent.append(generateFields(stepType));
        
        // Add constructor
        classContent.append(generateConstructor(stepType));
        
        // Add step definition methods
        for (StepDefinition definition : definitions) {
            classContent.append("    ").append(definition.getAnnotation()).append("\n");
            classContent.append("    ").append(definition.getMethodSignature()).append(" {\n");
            classContent.append("        ").append(definition.getMethodBody()).append("\n");
            classContent.append("    }\n\n");
        }
        
        classContent.append("}\n");
        
        return classContent.toString();
    }
    
    /**
     * Generate imports based on step type
     */
    private String generateImports(String stepType) {
        StringBuilder imports = new StringBuilder();
        
        imports.append("import io.cucumber.java.en.*;\n");
        imports.append("import io.cucumber.datatable.DataTable;\n");
        imports.append("import org.testng.Assert;\n");
        imports.append("import org.slf4j.Logger;\n");
        imports.append("import org.slf4j.LoggerFactory;\n");
        
        switch (stepType) {
            case "WEB":
                imports.append("import org.openqa.selenium.WebDriver;\n");
                imports.append("import org.openqa.selenium.WebElement;\n");
                imports.append("import org.openqa.selenium.By;\n");
                imports.append("import org.openqa.selenium.support.ui.WebDriverWait;\n");
                imports.append("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
                break;
            case "API":
                imports.append("import io.restassured.RestAssured;\n");
                imports.append("import io.restassured.response.Response;\n");
                imports.append("import io.restassured.specification.RequestSpecification;\n");
                break;
            case "MOBILE":
                imports.append("import io.appium.java_client.AppiumDriver;\n");
                imports.append("import io.appium.java_client.MobileElement;\n");
                break;
        }
        
        imports.append("\n");
        return imports.toString();
    }
    
    /**
     * Generate class fields
     */
    private String generateFields(String stepType) {
        StringBuilder fields = new StringBuilder();
        
        // Add logger field for all step types
        fields.append("    private static final Logger logger = LoggerFactory.getLogger(");
        fields.append(stepType).append("StepDefinitions.class);\n");
        
        switch (stepType) {
            case "WEB":
                fields.append("    private WebDriver driver;\n");
                fields.append("    private WebDriverWait wait;\n");
                break;
            case "API":
                fields.append("    private RequestSpecification request;\n");
                fields.append("    private Response response;\n");
                break;
            case "MOBILE":
                fields.append("    private AppiumDriver<MobileElement> driver;\n");
                break;
        }
        
        fields.append("\n");
        return fields.toString();
    }
    
    /**
     * Generate constructor
     */
    private String generateConstructor(String stepType) {
        StringBuilder constructor = new StringBuilder();
        
        switch (stepType) {
            case "WEB":
                constructor.append("    public WEBStepDefinitions() {\n");
                constructor.append("        // Initialize WebDriver\n");
                constructor.append("        // this.driver = DriverManager.getDriver();\n");
                constructor.append("        // this.wait = new WebDriverWait(driver, 10);\n");
                constructor.append("    }\n\n");
                break;
            case "API":
                constructor.append("    public ApiStepDefinitions() {\n");
                constructor.append("        // Initialize REST Assured\n");
                constructor.append("        // this.request = RestAssured.given();\n");
                constructor.append("    }\n\n");
                break;
            case "MOBILE":
                constructor.append("    public MobileStepDefinitions() {\n");
                constructor.append("        // Initialize Appium Driver\n");
                constructor.append("        // this.driver = MobileDriverManager.getDriver();\n");
                constructor.append("    }\n\n");
                break;
            default:
                constructor.append("    public CommonStepDefinitions() {\n");
                constructor.append("        // Initialize common dependencies\n");
                constructor.append("    }\n\n");
                break;
        }
        
        return constructor.toString();
    }
    
    // Helper methods
    private Set<FeatureStep> getUniqueSteps(Map<String, List<FeatureStep>> allSteps) {
        Set<FeatureStep> uniqueSteps = new HashSet<>();
        for (List<FeatureStep> steps : allSteps.values()) {
            uniqueSteps.addAll(steps);
        }
        return uniqueSteps;
    }
    
    private Map<String, List<FeatureStep>> groupStepsByType(Set<FeatureStep> steps) {
        return steps.stream().collect(Collectors.groupingBy(FeatureStep::getStepType));
    }
    
    private boolean shouldGenerateForStepType(String stepType, GenerationConfig config) {
        switch (stepType) {
            case "WEB": return config.isGeneratePageObjectMethods();
            case "API": return config.isGenerateApiMethods();
            case "MOBILE": return config.isGenerateMobileMethods();
            case "COMMON": return config.isGenerateCommonMethods();
            default: return true;
        }
    }
}
