package com.testautomation.agent.parser;

import com.testautomation.agent.model.FeatureStep;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for Cucumber feature files to extract steps and parameters
 */
public class FeatureFileParser {
    private static final Logger logger = LoggerFactory.getLogger(FeatureFileParser.class);
    
    private static final Pattern STEP_PATTERN = Pattern.compile("^(Given|When|Then|And|But)\\s+(.+)$");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]+)\"|(\\d+)");
    private static final Pattern DATA_TABLE_PATTERN = Pattern.compile("^\\s*\\|.*\\|\\s*$");
    private static final Pattern DOC_STRING_PATTERN = Pattern.compile("^\\s*\"\"\"");
    
    /**
     * Parse all feature files in a directory
     */
    public Map<String, List<FeatureStep>> parseFeatureFiles(String directoryPath) throws IOException {
        Map<String, List<FeatureStep>> allSteps = new HashMap<>();
        
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Directory does not exist: " + directoryPath);
        }
        
        Collection<File> featureFiles = FileUtils.listFiles(directory, new String[]{"feature"}, true);
        
        for (File featureFile : featureFiles) {
            try {
                List<FeatureStep> steps = parseFeatureFile(featureFile.getAbsolutePath());
                allSteps.put(featureFile.getName(), steps);
                logger.info("Parsed {} steps from {}", steps.size(), featureFile.getName());
            } catch (IOException e) {
                logger.error("Error parsing feature file: {}", featureFile.getName(), e);
            }
        }
        
        return allSteps;
    }
    
    /**
     * Parse a single feature file
     */
    public List<FeatureStep> parseFeatureFile(String filePath) throws IOException {
        List<FeatureStep> steps = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File(filePath), StandardCharsets.UTF_8);
        
        String currentFeatureFile = new File(filePath).getName();
        boolean inScenario = false;
        boolean inDataTable = false;
        boolean inDocString = false;
        StringBuilder docStringContent = new StringBuilder();
        
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            int lineNumber = i + 1;
            
            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            // Check for scenario start
            if (line.startsWith("Scenario:") || line.startsWith("Scenario Outline:")) {
                inScenario = true;
                continue;
            }
            
            // Check for feature end
            if (line.startsWith("Feature:") && inScenario) {
                inScenario = false;
            }
            
            // Only process steps within scenarios
            if (!inScenario) {
                continue;
            }
            
            // Handle doc strings
            if (DOC_STRING_PATTERN.matcher(line).matches()) {
                if (inDocString) {
                    inDocString = false;
                    // Process the collected doc string content
                    docStringContent = new StringBuilder();
                } else {
                    inDocString = true;
                }
                continue;
            }
            
            if (inDocString) {
                docStringContent.append(line).append("\n");
                continue;
            }
            
            // Handle data tables
            if (DATA_TABLE_PATTERN.matcher(line).matches()) {
                inDataTable = true;
                continue;
            } else if (inDataTable && !DATA_TABLE_PATTERN.matcher(line).matches()) {
                inDataTable = false;
            }
            
            // Parse step
            Matcher stepMatcher = STEP_PATTERN.matcher(line);
            if (stepMatcher.matches()) {
                String keyword = stepMatcher.group(1);
                String stepText = stepMatcher.group(2);
                
                // Extract parameters from step text
                List<String> parameters = extractParameters(stepText);
                
                // Create feature step
                FeatureStep step = new FeatureStep(keyword, stepText, parameters, currentFeatureFile, lineNumber);
                steps.add(step);
                
                logger.debug("Parsed step: {} {}", keyword, stepText);
            }
        }
        
        return steps;
    }
    
    /**
     * Extract parameters from step text
     */
    private List<String> parameters = new ArrayList<>();
    
    private List<String> extractParameters(String stepText) {
        List<String> parameters = new ArrayList<>();
        
        // Extract quoted strings
        Matcher quotedMatcher = Pattern.compile("\"([^\"]+)\"").matcher(stepText);
        while (quotedMatcher.find()) {
            parameters.add(quotedMatcher.group(1));
        }
        
        // Extract numbers
        Matcher numberMatcher = Pattern.compile("\\b(\\d+)\\b").matcher(stepText);
        while (numberMatcher.find()) {
            parameters.add(numberMatcher.group(1));
        }
        
        // Extract data table references
        if (stepText.contains("data table")) {
            parameters.add("DataTable");
        }
        
        // Extract doc string references
        if (stepText.contains("doc string") || stepText.contains("multiline")) {
            parameters.add("String");
        }
        
        return parameters;
    }
    
    /**
     * Get unique steps across all feature files
     */
    public Set<FeatureStep> getUniqueSteps(Map<String, List<FeatureStep>> allSteps) {
        Set<FeatureStep> uniqueSteps = new HashSet<>();
        
        for (List<FeatureStep> steps : allSteps.values()) {
            uniqueSteps.addAll(steps);
        }
        
        return uniqueSteps;
    }
    
    /**
     * Get duplicate steps across feature files
     */
    public Map<FeatureStep, List<String>> getDuplicateSteps(Map<String, List<FeatureStep>> allSteps) {
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
        
        return duplicateSteps;
    }
    
    /**
     * Group steps by type
     */
    public Map<String, List<FeatureStep>> groupStepsByType(Set<FeatureStep> steps) {
        Map<String, List<FeatureStep>> groupedSteps = new HashMap<>();
        
        for (FeatureStep step : steps) {
            groupedSteps.computeIfAbsent(step.getStepType(), k -> new ArrayList<>()).add(step);
        }
        
        return groupedSteps;
    }
}
