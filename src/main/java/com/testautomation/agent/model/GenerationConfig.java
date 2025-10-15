package com.testautomation.agent.model;

import java.util.List;
import java.util.Map;

/**
 * Configuration for step definition generation
 */
public class GenerationConfig {
    private String outputDirectory;
    private String basePackage;
    private boolean generatePageObjectMethods;
    private boolean generateApiMethods;
    private boolean generateMobileMethods;
    private boolean generateCommonMethods;
    private String templateDirectory;
    private Map<String, String> customTemplates;
    private List<String> excludedSteps;
    private boolean validateSteps;
    private boolean generateReports;
    private String reportFormat; // HTML, JSON, XML
    private boolean organizeByFeature;
    private boolean organizeByStepType;
    private String namingConvention; // CAMEL_CASE, SNAKE_CASE, PASCAL_CASE

    public GenerationConfig() {
        // Default values
        this.outputDirectory = "src/test/java/stepdefinitions";
        this.basePackage = "com.testautomation.stepdefinitions";
        this.generatePageObjectMethods = true;
        this.generateApiMethods = true;
        this.generateMobileMethods = true;
        this.generateCommonMethods = true;
        this.validateSteps = true;
        this.generateReports = true;
        this.reportFormat = "HTML";
        this.organizeByStepType = true;
        this.namingConvention = "CAMEL_CASE";
    }

    // Getters and Setters
    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }

    public String getBasePackage() { return basePackage; }
    public void setBasePackage(String basePackage) { this.basePackage = basePackage; }

    public boolean isGeneratePageObjectMethods() { return generatePageObjectMethods; }
    public void setGeneratePageObjectMethods(boolean generatePageObjectMethods) { 
        this.generatePageObjectMethods = generatePageObjectMethods; 
    }

    public boolean isGenerateApiMethods() { return generateApiMethods; }
    public void setGenerateApiMethods(boolean generateApiMethods) { 
        this.generateApiMethods = generateApiMethods; 
    }

    public boolean isGenerateMobileMethods() { return generateMobileMethods; }
    public void setGenerateMobileMethods(boolean generateMobileMethods) { 
        this.generateMobileMethods = generateMobileMethods; 
    }

    public boolean isGenerateCommonMethods() { return generateCommonMethods; }
    public void setGenerateCommonMethods(boolean generateCommonMethods) { 
        this.generateCommonMethods = generateCommonMethods; 
    }

    public String getTemplateDirectory() { return templateDirectory; }
    public void setTemplateDirectory(String templateDirectory) { this.templateDirectory = templateDirectory; }

    public Map<String, String> getCustomTemplates() { return customTemplates; }
    public void setCustomTemplates(Map<String, String> customTemplates) { this.customTemplates = customTemplates; }

    public List<String> getExcludedSteps() { return excludedSteps; }
    public void setExcludedSteps(List<String> excludedSteps) { this.excludedSteps = excludedSteps; }

    public boolean isValidateSteps() { return validateSteps; }
    public void setValidateSteps(boolean validateSteps) { this.validateSteps = validateSteps; }

    public boolean isGenerateReports() { return generateReports; }
    public void setGenerateReports(boolean generateReports) { this.generateReports = generateReports; }

    public String getReportFormat() { return reportFormat; }
    public void setReportFormat(String reportFormat) { this.reportFormat = reportFormat; }

    public boolean isOrganizeByFeature() { return organizeByFeature; }
    public void setOrganizeByFeature(boolean organizeByFeature) { this.organizeByFeature = organizeByFeature; }

    public boolean isOrganizeByStepType() { return organizeByStepType; }
    public void setOrganizeByStepType(boolean organizeByStepType) { this.organizeByStepType = organizeByStepType; }

    public String getNamingConvention() { return namingConvention; }
    public void setNamingConvention(String namingConvention) { this.namingConvention = namingConvention; }
}
