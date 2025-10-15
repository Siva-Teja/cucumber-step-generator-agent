package com.testautomation.agent.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testautomation.agent.model.FeatureStep;
import com.testautomation.agent.model.StepDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generates various reports for step definition generation
 */
public class ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    
    private final ObjectMapper objectMapper;
    
    public ReportGenerator() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Generate HTML report
     */
    public void generateHtmlReport(Map<String, List<FeatureStep>> allSteps,
                                 Map<String, List<StepDefinition>> generatedDefinitions,
                                 Map<FeatureStep, List<String>> duplicates,
                                 String outputPath) throws IOException {
        
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Cucumber Step Definition Generation Report</title>\n");
        html.append("<style>\n");
        html.append(getHtmlStyles());
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<div class=\"container\">\n");
        html.append("<h1>Cucumber Step Definition Generation Report</h1>\n");
        html.append("<p class=\"timestamp\">Generated on: " + getCurrentTimestamp() + "</p>\n");
        
        // Summary section
        html.append("<div class=\"section\">\n");
        html.append("<h2>Summary</h2>\n");
        html.append(generateSummaryHtml(allSteps, generatedDefinitions, duplicates));
        html.append("</div>\n");
        
        // Feature files section
        html.append("<div class=\"section\">\n");
        html.append("<h2>Feature Files Analysis</h2>\n");
        html.append(generateFeatureFilesHtml(allSteps));
        html.append("</div>\n");
        
        // Generated definitions section
        html.append("<div class=\"section\">\n");
        html.append("<h2>Generated Step Definitions</h2>\n");
        html.append(generateDefinitionsHtml(generatedDefinitions));
        html.append("</div>\n");
        
        // Duplicates section
        html.append("<div class=\"section\">\n");
        html.append("<h2>Duplicate Steps</h2>\n");
        html.append(generateDuplicatesHtml(duplicates));
        html.append("</div>\n");
        
        html.append("</div>\n");
        html.append("</body>\n</html>");
        
        // Write HTML file
        File file = new File(outputPath);
        org.apache.commons.io.FileUtils.writeStringToFile(file, html.toString(), StandardCharsets.UTF_8);
        logger.info("HTML report generated: {}", outputPath);
    }
    
    /**
     * Generate JSON report
     */
    public void generateJsonReport(Map<String, List<FeatureStep>> allSteps,
                                 Map<String, List<StepDefinition>> generatedDefinitions,
                                 Map<FeatureStep, List<String>> duplicates,
                                 String outputPath) throws IOException {
        
        Map<String, Object> report = new HashMap<>();
        report.put("timestamp", getCurrentTimestamp());
        report.put("summary", generateSummaryData(allSteps, generatedDefinitions, duplicates));
        report.put("featureFiles", allSteps);
        report.put("generatedDefinitions", generatedDefinitions);
        report.put("duplicates", duplicates);
        
        objectMapper.writeValue(new File(outputPath), report);
        logger.info("JSON report generated: {}", outputPath);
    }
    
    /**
     * Generate XML report
     */
    public void generateXmlReport(Map<String, List<FeatureStep>> allSteps,
                               Map<String, List<StepDefinition>> generatedDefinitions,
                               Map<FeatureStep, List<String>> duplicates,
                               String outputPath) throws IOException {
        
        StringBuilder xml = new StringBuilder();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<stepDefinitionReport>\n");
        xml.append("<timestamp>").append(getCurrentTimestamp()).append("</timestamp>\n");
        
        // Summary
        xml.append("<summary>\n");
        Map<String, Object> summary = generateSummaryData(allSteps, generatedDefinitions, duplicates);
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            xml.append("<").append(entry.getKey()).append(">")
               .append(entry.getValue())
               .append("</").append(entry.getKey()).append(">\n");
        }
        xml.append("</summary>\n");
        
        // Feature files
        xml.append("<featureFiles>\n");
        for (Map.Entry<String, List<FeatureStep>> entry : allSteps.entrySet()) {
            xml.append("<featureFile name=\"").append(entry.getKey()).append("\">\n");
            for (FeatureStep step : entry.getValue()) {
                xml.append("<step>\n");
                xml.append("<keyword>").append(step.getKeyword()).append("</keyword>\n");
                xml.append("<text>").append(step.getText()).append("</text>\n");
                xml.append("<type>").append(step.getStepType()).append("</type>\n");
                xml.append("</step>\n");
            }
            xml.append("</featureFile>\n");
        }
        xml.append("</featureFiles>\n");
        
        xml.append("</stepDefinitionReport>");
        
        // Write XML file
        File file = new File(outputPath);
        org.apache.commons.io.FileUtils.writeStringToFile(file, xml.toString(), StandardCharsets.UTF_8);
        logger.info("XML report generated: {}", outputPath);
    }
    
    /**
     * Generate console report
     */
    public void generateConsoleReport(Map<String, List<FeatureStep>> allSteps,
                                    Map<String, List<StepDefinition>> generatedDefinitions,
                                    Map<FeatureStep, List<String>> duplicates) {
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CUCUMBER STEP DEFINITION GENERATION REPORT");
        System.out.println("=".repeat(80));
        System.out.println("Generated on: " + getCurrentTimestamp());
        System.out.println();
        
        // Summary
        System.out.println("SUMMARY:");
        System.out.println("-".repeat(40));
        Map<String, Object> summary = generateSummaryData(allSteps, generatedDefinitions, duplicates);
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            System.out.printf("%-20s: %s%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
        
        // Feature files
        System.out.println("FEATURE FILES:");
        System.out.println("-".repeat(40));
        for (Map.Entry<String, List<FeatureStep>> entry : allSteps.entrySet()) {
            System.out.printf("%-30s: %d steps%n", entry.getKey(), entry.getValue().size());
        }
        System.out.println();
        
        // Generated definitions
        System.out.println("GENERATED DEFINITIONS:");
        System.out.println("-".repeat(40));
        for (Map.Entry<String, List<StepDefinition>> entry : generatedDefinitions.entrySet()) {
            System.out.printf("%-20s: %d methods%n", entry.getKey(), entry.getValue().size());
        }
        System.out.println();
        
        // Duplicates
        if (!duplicates.isEmpty()) {
            System.out.println("DUPLICATE STEPS:");
            System.out.println("-".repeat(40));
            for (Map.Entry<FeatureStep, List<String>> entry : duplicates.entrySet()) {
                FeatureStep step = entry.getKey();
                List<String> files = entry.getValue();
                System.out.printf("%s %s%n", step.getKeyword(), step.getText());
                System.out.printf("  Found in: %s%n", String.join(", ", files));
            }
            System.out.println();
        }
        
        System.out.println("=".repeat(80));
    }
    
    // Helper methods
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private Map<String, Object> generateSummaryData(Map<String, List<FeatureStep>> allSteps,
                                                  Map<String, List<StepDefinition>> generatedDefinitions,
                                                  Map<FeatureStep, List<String>> duplicates) {
        Map<String, Object> summary = new HashMap<>();
        
        int totalSteps = allSteps.values().stream().mapToInt(List::size).sum();
        int totalDefinitions = generatedDefinitions.values().stream().mapToInt(List::size).sum();
        
        summary.put("Total Feature Files", allSteps.size());
        summary.put("Total Steps", totalSteps);
        summary.put("Generated Definitions", totalDefinitions);
        summary.put("Duplicate Steps", duplicates.size());
        summary.put("Unique Steps", totalSteps - duplicates.size());
        
        return summary;
    }
    
    private String generateSummaryHtml(Map<String, List<FeatureStep>> allSteps,
                                     Map<String, List<StepDefinition>> generatedDefinitions,
                                     Map<FeatureStep, List<String>> duplicates) {
        Map<String, Object> summary = generateSummaryData(allSteps, generatedDefinitions, duplicates);
        
        StringBuilder html = new StringBuilder();
        html.append("<table class=\"summary-table\">\n");
        for (Map.Entry<String, Object> entry : summary.entrySet()) {
            html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
        }
        html.append("</table>\n");
        
        return html.toString();
    }
    
    private String generateFeatureFilesHtml(Map<String, List<FeatureStep>> allSteps) {
        StringBuilder html = new StringBuilder();
        html.append("<table class=\"data-table\">\n");
        html.append("<tr><th>Feature File</th><th>Steps</th><th>Types</th></tr>\n");
        
        for (Map.Entry<String, List<FeatureStep>> entry : allSteps.entrySet()) {
            String fileName = entry.getKey();
            List<FeatureStep> steps = entry.getValue();
            
            Map<String, Long> typeCounts = steps.stream()
                .collect(java.util.stream.Collectors.groupingBy(FeatureStep::getStepType, java.util.stream.Collectors.counting()));
            
            html.append("<tr>");
            html.append("<td>").append(fileName).append("</td>");
            html.append("<td>").append(steps.size()).append("</td>");
            html.append("<td>").append(typeCounts.toString()).append("</td>");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        return html.toString();
    }
    
    private String generateDefinitionsHtml(Map<String, List<StepDefinition>> generatedDefinitions) {
        StringBuilder html = new StringBuilder();
        html.append("<table class=\"data-table\">\n");
        html.append("<tr><th>Step Type</th><th>Methods Generated</th><th>Classes</th></tr>\n");
        
        for (Map.Entry<String, List<StepDefinition>> entry : generatedDefinitions.entrySet()) {
            String stepType = entry.getKey();
            List<StepDefinition> definitions = entry.getValue();
            
            Set<String> classes = definitions.stream()
                .map(StepDefinition::getClassName)
                .collect(java.util.stream.Collectors.toSet());
            
            html.append("<tr>");
            html.append("<td>").append(stepType).append("</td>");
            html.append("<td>").append(definitions.size()).append("</td>");
            html.append("<td>").append(classes.size()).append("</td>");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        return html.toString();
    }
    
    private String generateDuplicatesHtml(Map<FeatureStep, List<String>> duplicates) {
        if (duplicates.isEmpty()) {
            return "<p class=\"no-duplicates\">No duplicate steps found.</p>";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<table class=\"data-table\">\n");
        html.append("<tr><th>Step</th><th>Type</th><th>Found In Files</th></tr>\n");
        
        for (Map.Entry<FeatureStep, List<String>> entry : duplicates.entrySet()) {
            FeatureStep step = entry.getKey();
            List<String> files = entry.getValue();
            
            html.append("<tr>");
            html.append("<td>").append(step.getKeyword()).append(" ").append(step.getText()).append("</td>");
            html.append("<td>").append(step.getStepType()).append("</td>");
            html.append("<td>").append(String.join(", ", files)).append("</td>");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        return html.toString();
    }
    
    private String getHtmlStyles() {
        return "body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n" +
               ".container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n" +
               "h1 { color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; }\n" +
               "h2 { color: #555; margin-top: 30px; }\n" +
               ".timestamp { color: #666; font-style: italic; }\n" +
               ".section { margin-bottom: 30px; }\n" +
               "table { width: 100%; border-collapse: collapse; margin-top: 10px; }\n" +
               "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n" +
               "th { background-color: #f8f9fa; font-weight: bold; }\n" +
               ".summary-table td:first-child { font-weight: bold; }\n" +
               ".no-duplicates { color: #28a745; font-weight: bold; }\n" +
               "tr:nth-child(even) { background-color: #f9f9f9; }";
    }
}
