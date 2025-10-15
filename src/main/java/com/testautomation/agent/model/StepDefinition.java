package com.testautomation.agent.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a generated step definition method
 */
public class StepDefinition {
    private String methodName;
    private String annotation;
    private String methodSignature;
    private String methodBody;
    private String className;
    private String packageName;
    private List<String> parameters;
    private String stepType;
    private String originalStepText;
    private String filePath;

    public StepDefinition() {}

    public StepDefinition(String methodName, String annotation, String methodSignature, 
                         String methodBody, String className, String packageName, 
                         List<String> parameters, String stepType, String originalStepText) {
        this.methodName = methodName;
        this.annotation = annotation;
        this.methodSignature = methodSignature;
        this.methodBody = methodBody;
        this.className = className;
        this.packageName = packageName;
        this.parameters = parameters;
        this.stepType = stepType;
        this.originalStepText = originalStepText;
    }

    // Getters and Setters
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }

    public String getAnnotation() { return annotation; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }

    public String getMethodSignature() { return methodSignature; }
    public void setMethodSignature(String methodSignature) { this.methodSignature = methodSignature; }

    public String getMethodBody() { return methodBody; }
    public void setMethodBody(String methodBody) { this.methodBody = methodBody; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }

    public String getStepType() { return stepType; }
    public void setStepType(String stepType) { this.stepType = stepType; }

    public String getOriginalStepText() { return originalStepText; }
    public void setOriginalStepText(String originalStepText) { this.originalStepText = originalStepText; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StepDefinition that = (StepDefinition) o;
        return Objects.equals(annotation, that.annotation) && 
               Objects.equals(originalStepText, that.originalStepText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, originalStepText);
    }

    @Override
    public String toString() {
        return "StepDefinition{" +
                "methodName='" + methodName + '\'' +
                ", annotation='" + annotation + '\'' +
                ", className='" + className + '\'' +
                ", stepType='" + stepType + '\'' +
                ", originalStepText='" + originalStepText + '\'' +
                '}';
    }
}
