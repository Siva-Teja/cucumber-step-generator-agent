package com.testautomation.agent.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a step from a Cucumber feature file
 */
public class FeatureStep {
    private String keyword; // Given, When, Then, And, But
    private String text;
    private List<String> parameters;
    private String featureFile;
    private int lineNumber;
    private String stepType; // WEB, API, MOBILE, COMMON

    public FeatureStep() {}

    public FeatureStep(String keyword, String text, List<String> parameters, String featureFile, int lineNumber) {
        this.keyword = keyword;
        this.text = text;
        this.parameters = parameters;
        this.featureFile = featureFile;
        this.lineNumber = lineNumber;
        this.stepType = determineStepType(text);
    }

    private String determineStepType(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.contains("api") || lowerText.contains("request") || lowerText.contains("response")) {
            return "API";
        } else if (lowerText.contains("mobile") || lowerText.contains("app") || lowerText.contains("device")) {
            return "MOBILE";
        } else if (lowerText.contains("page") || lowerText.contains("element") || lowerText.contains("click") || 
                   lowerText.contains("input") || lowerText.contains("verify") || lowerText.contains("navigate")) {
            return "WEB";
        } else {
            return "COMMON";
        }
    }

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }

    public String getFeatureFile() { return featureFile; }
    public void setFeatureFile(String featureFile) { this.featureFile = featureFile; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public String getStepType() { return stepType; }
    public void setStepType(String stepType) { this.stepType = stepType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureStep that = (FeatureStep) o;
        return Objects.equals(keyword, that.keyword) && Objects.equals(normalizeText(text), normalizeText(that.text));
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, normalizeText(text));
    }
    
    /**
     * Normalize step text by replacing parameters with placeholders for comparison
     */
    private String normalizeText(String text) {
        if (text == null) return null;
        
        // Replace quoted strings with placeholder
        text = text.replaceAll("\"([^\"]+)\"", "\"{string}\"");
        
        // Replace numbers with placeholder
        text = text.replaceAll("\\b(\\d+)\\b", "{int}");
        
        // Replace data table references
        text = text.replaceAll("data table", "{datatable}");
        
        // Replace doc string references
        text = text.replaceAll("doc string|multiline", "{docstring}");
        
        return text;
    }

    @Override
    public String toString() {
        return "FeatureStep{" +
                "keyword='" + keyword + '\'' +
                ", text='" + text + '\'' +
                ", parameters=" + parameters +
                ", featureFile='" + featureFile + '\'' +
                ", lineNumber=" + lineNumber +
                ", stepType='" + stepType + '\'' +
                '}';
    }
}
