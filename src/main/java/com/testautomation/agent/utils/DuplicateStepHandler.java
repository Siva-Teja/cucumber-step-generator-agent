package com.testautomation.agent.utils;

import com.testautomation.agent.model.FeatureStep;
import com.testautomation.agent.model.StepDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles duplicate step detection and organization
 */
public class DuplicateStepHandler {
    private static final Logger logger = LoggerFactory.getLogger(DuplicateStepHandler.class);
    
    /**
     * Detect duplicate steps across feature files
     */
    public Map<FeatureStep, List<String>> detectDuplicates(Map<String, List<FeatureStep>> allSteps) {
        Map<FeatureStep, List<String>> duplicateSteps = new HashMap<>();
        Map<FeatureStep, List<String>> stepOccurrences = new HashMap<>();
        
        // Count occurrences of each step
        for (Map.Entry<String, List<FeatureStep>> entry : allSteps.entrySet()) {
            String featureFile = entry.getKey();
            List<FeatureStep> steps = entry.getValue();
            
            for (FeatureStep step : steps) {
                stepOccurrences.computeIfAbsent(step, k -> new ArrayList<>()).add(featureFile);
            }
        }
        
        // Find duplicates
        for (Map.Entry<FeatureStep, List<String>> entry : stepOccurrences.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicateSteps.put(entry.getKey(), entry.getValue());
            }
        }
        
        logger.info("Found {} duplicate steps across feature files", duplicateSteps.size());
        return duplicateSteps;
    }
    
    /**
     * Organize steps to avoid duplicates and consolidate common steps
     */
    public Map<String, List<FeatureStep>> organizeSteps(Map<String, List<FeatureStep>> allSteps) {
        Map<String, List<FeatureStep>> organizedSteps = new HashMap<>();
        
        // Get all unique steps (this now properly handles duplicates across feature files)
        Set<FeatureStep> uniqueSteps = getAllUniqueSteps(allSteps);
        
        // Detect steps that appear in multiple feature files
        Map<FeatureStep, List<String>> duplicateSteps = detectDuplicates(allSteps);
        
        // Separate steps into categories
        List<FeatureStep> commonSteps = new ArrayList<>();
        List<FeatureStep> webSteps = new ArrayList<>();
        List<FeatureStep> apiSteps = new ArrayList<>();
        List<FeatureStep> mobileSteps = new ArrayList<>();
        
        for (FeatureStep step : uniqueSteps) {
            // If step appears in multiple feature files, move it to COMMON
            if (duplicateSteps.containsKey(step)) {
                logger.debug("Moving step '{}' to COMMON as it appears in multiple files: {}", 
                    step.getText(), duplicateSteps.get(step));
                commonSteps.add(step);
            } else {
                // Otherwise, keep it in its original category
                switch (step.getStepType()) {
                    case "WEB":
                        webSteps.add(step);
                        break;
                    case "API":
                        apiSteps.add(step);
                        break;
                    case "MOBILE":
                        mobileSteps.add(step);
                        break;
                    case "COMMON":
                    default:
                        commonSteps.add(step);
                        break;
                }
            }
        }
        
        // Add organized steps
        if (!commonSteps.isEmpty()) {
            organizedSteps.put("COMMON", commonSteps);
        }
        if (!webSteps.isEmpty()) {
            organizedSteps.put("WEB", webSteps);
        }
        if (!apiSteps.isEmpty()) {
            organizedSteps.put("API", apiSteps);
        }
        if (!mobileSteps.isEmpty()) {
            organizedSteps.put("MOBILE", mobileSteps);
        }
        
        logger.info("Organized steps: COMMON={}, WEB={}, API={}, MOBILE={}", 
            commonSteps.size(), webSteps.size(), apiSteps.size(), mobileSteps.size());
        
        return organizedSteps;
    }
    
    /**
     * Merge duplicate step definitions
     */
    public List<StepDefinition> mergeDuplicateDefinitions(List<StepDefinition> definitions) {
        Map<String, StepDefinition> uniqueDefinitions = new HashMap<>();
        
        for (StepDefinition definition : definitions) {
            String key = definition.getAnnotation() + "|" + definition.getOriginalStepText();
            
            if (!uniqueDefinitions.containsKey(key)) {
                uniqueDefinitions.put(key, definition);
            } else {
                // Merge parameters if needed
                StepDefinition existing = uniqueDefinitions.get(key);
                List<String> mergedParams = mergeParameters(existing.getParameters(), definition.getParameters());
                existing.setParameters(mergedParams);
                
                logger.debug("Merged duplicate step definition: {}", definition.getMethodName());
            }
        }
        
        return new ArrayList<>(uniqueDefinitions.values());
    }
    
    /**
     * Merge parameter lists
     */
    private List<String> mergeParameters(List<String> params1, List<String> params2) {
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(params1);
        merged.addAll(params2);
        return new ArrayList<>(merged);
    }
    
    /**
     * Generate step organization report
     */
    public String generateOrganizationReport(Map<FeatureStep, List<String>> duplicates, 
                                          Map<String, List<FeatureStep>> organizedSteps) {
        StringBuilder report = new StringBuilder();
        
        report.append("=== STEP ORGANIZATION REPORT ===\n\n");
        
        // Duplicate steps section
        report.append("DUPLICATE STEPS FOUND:\n");
        if (duplicates.isEmpty()) {
            report.append("No duplicate steps found.\n\n");
        } else {
            for (Map.Entry<FeatureStep, List<String>> entry : duplicates.entrySet()) {
                FeatureStep step = entry.getKey();
                List<String> featureFiles = entry.getValue();
                
                report.append(String.format("- Step: %s %s\n", step.getKeyword(), step.getText()));
                report.append(String.format("  Found in files: %s\n", String.join(", ", featureFiles)));
                report.append(String.format("  Step type: %s\n", step.getStepType()));
                report.append("\n");
            }
        }
        
        // Organization summary
        report.append("STEP ORGANIZATION SUMMARY:\n");
        for (Map.Entry<String, List<FeatureStep>> entry : organizedSteps.entrySet()) {
            String stepType = entry.getKey();
            List<FeatureStep> steps = entry.getValue();
            
            report.append(String.format("- %s steps: %d\n", stepType, steps.size()));
        }
        
        return report.toString();
    }
    
    /**
     * Validate step definitions for conflicts
     */
    public List<String> validateStepDefinitions(List<StepDefinition> definitions) {
        List<String> validationErrors = new ArrayList<>();
        Map<String, List<StepDefinition>> annotationGroups = new HashMap<>();
        
        // Group by annotation
        for (StepDefinition definition : definitions) {
            annotationGroups.computeIfAbsent(definition.getAnnotation(), k -> new ArrayList<>())
                           .add(definition);
        }
        
        // Check for conflicts
        for (Map.Entry<String, List<StepDefinition>> entry : annotationGroups.entrySet()) {
            List<StepDefinition> group = entry.getValue();
            if (group.size() > 1) {
                validationErrors.add(String.format("Duplicate annotation found: %s (used in %d methods)", 
                    entry.getKey(), group.size()));
            }
        }
        
        // Check method names
        Set<String> methodNames = new HashSet<>();
        for (StepDefinition definition : definitions) {
            if (!methodNames.add(definition.getMethodName())) {
                validationErrors.add(String.format("Duplicate method name: %s", definition.getMethodName()));
            }
        }
        
        return validationErrors;
    }
    
    /**
     * Suggest step organization strategy
     */
    public String suggestOrganizationStrategy(Map<FeatureStep, List<String>> duplicates) {
        StringBuilder suggestions = new StringBuilder();
        
        suggestions.append("=== ORGANIZATION SUGGESTIONS ===\n\n");
        
        if (duplicates.isEmpty()) {
            suggestions.append("No duplicates found. Current organization is optimal.\n");
            return suggestions.toString();
        }
        
        suggestions.append("RECOMMENDED ACTIONS:\n\n");
        
        // Group duplicates by step type
        Map<String, List<FeatureStep>> duplicatesByType = new HashMap<>();
        for (FeatureStep step : duplicates.keySet()) {
            duplicatesByType.computeIfAbsent(step.getStepType(), k -> new ArrayList<>()).add(step);
        }
        
        for (Map.Entry<String, List<FeatureStep>> entry : duplicatesByType.entrySet()) {
            String stepType = entry.getKey();
            List<FeatureStep> steps = entry.getValue();
            
            suggestions.append(String.format("1. %s Steps (%d duplicates):\n", stepType, steps.size()));
            suggestions.append("   - Move to common step definition file\n");
            suggestions.append("   - Ensure consistent parameter handling\n");
            suggestions.append("   - Consider creating reusable helper methods\n\n");
        }
        
        suggestions.append("GENERAL RECOMMENDATIONS:\n");
        suggestions.append("- Use Page Object Model for web steps\n");
        suggestions.append("- Create API client classes for API steps\n");
        suggestions.append("- Implement mobile page objects for mobile steps\n");
        suggestions.append("- Use dependency injection for shared resources\n");
        
        return suggestions.toString();
    }
    
    // Helper methods
    private Set<FeatureStep> getAllUniqueSteps(Map<String, List<FeatureStep>> allSteps) {
        Set<FeatureStep> uniqueSteps = new HashSet<>();
        for (List<FeatureStep> steps : allSteps.values()) {
            uniqueSteps.addAll(steps);
        }
        return uniqueSteps;
    }
    
    private Map<String, List<FeatureStep>> groupStepsByType(Set<FeatureStep> steps) {
        return steps.stream().collect(Collectors.groupingBy(FeatureStep::getStepType));
    }
}
