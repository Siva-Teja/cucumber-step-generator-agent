package com.testautomation.agent.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages code templates for step definition generation
 */
public class CodeTemplateManager {
    private static final Logger logger = LoggerFactory.getLogger(CodeTemplateManager.class);
    
    private final Map<String, String> templates;
    
    public CodeTemplateManager() {
        this.templates = new HashMap<>();
        initializeDefaultTemplates();
    }
    
    /**
     * Initialize default templates for different step types
     */
    private void initializeDefaultTemplates() {
        // Web step template
        templates.put("WEB", 
            "// TODO: Implement web automation logic\n" +
            "// Example: driver.findElement(By.xpath(\"//button[@id='submit']\")).click();\n" +
            "logger.info(\"Executing web step: {}\");\n" +
            "throw new UnsupportedOperationException(\"Step not implemented yet\");");
        
        // API step template
        templates.put("API", 
            "// TODO: Implement API automation logic\n" +
            "// Example: response = request.when().get(\"/api/endpoint\");\n" +
            "logger.info(\"Executing API step: {}\");\n" +
            "throw new UnsupportedOperationException(\"Step not implemented yet\");");
        
        // Mobile step template
        templates.put("MOBILE", 
            "// TODO: Implement mobile automation logic\n" +
            "// Example: driver.findElement(By.id(\"button\")).click();\n" +
            "logger.info(\"Executing mobile step: {}\");\n" +
            "throw new UnsupportedOperationException(\"Step not implemented yet\");");
        
        // Common step template
        templates.put("COMMON", 
            "// TODO: Implement common step logic\n" +
            "logger.info(\"Executing common step: {}\");\n" +
            "throw new UnsupportedOperationException(\"Step not implemented yet\");");
    }
    
    /**
     * Get template for step type
     */
    public String getTemplate(String stepType) {
        return templates.getOrDefault(stepType, templates.get("COMMON"));
    }
    
    /**
     * Load templates from directory
     */
    public void loadTemplatesFromDirectory(String templateDirectory) throws IOException {
        File dir = new File(templateDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("Template directory does not exist: {}", templateDirectory);
            return;
        }
        
        File[] templateFiles = dir.listFiles((d, name) -> name.endsWith(".template"));
        if (templateFiles == null) {
            return;
        }
        
        for (File templateFile : templateFiles) {
            String stepType = templateFile.getName().replace(".template", "").toUpperCase();
            String content = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);
            templates.put(stepType, content);
            logger.info("Loaded template for step type: {}", stepType);
        }
    }
    
    /**
     * Process template with variables
     */
    public String processTemplate(String template, Map<String, String> variables) {
        String processedTemplate = template;
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            processedTemplate = processedTemplate.replace(placeholder, entry.getValue());
        }
        
        return processedTemplate;
    }
    
    /**
     * Add custom template
     */
    public void addTemplate(String stepType, String template) {
        templates.put(stepType.toUpperCase(), template);
    }
    
    /**
     * Get all available template types
     */
    public String[] getAvailableTemplateTypes() {
        return templates.keySet().toArray(new String[0]);
    }
}
