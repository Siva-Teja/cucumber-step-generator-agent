package com.testautomation.agent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager for the agent
 */
public class AgentConfig {
    private static final Logger logger = LoggerFactory.getLogger(AgentConfig.class);
    
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    
    public AgentConfig() {
        this.jsonMapper = new ObjectMapper();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    /**
     * Load configuration from JSON file
     */
    public Map<String, Object> loadFromJson(String configPath) throws IOException {
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            logger.warn("Configuration file not found: {}", configPath);
            return getDefaultConfig();
        }
        
        return jsonMapper.readValue(configFile, Map.class);
    }
    
    /**
     * Load configuration from YAML file
     */
    public Map<String, Object> loadFromYaml(String configPath) throws IOException {
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            logger.warn("Configuration file not found: {}", configPath);
            return getDefaultConfig();
        }
        
        return yamlMapper.readValue(configFile, Map.class);
    }
    
    /**
     * Save configuration to JSON file
     */
    public void saveToJson(Map<String, Object> config, String configPath) throws IOException {
        File configFile = new File(configPath);
        jsonMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
        logger.info("Configuration saved to: {}", configPath);
    }
    
    /**
     * Save configuration to YAML file
     */
    public void saveToYaml(Map<String, Object> config, String configPath) throws IOException {
        File configFile = new File(configPath);
        yamlMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, config);
        logger.info("Configuration saved to: {}", configPath);
    }
    
    /**
     * Get default configuration
     */
    public Map<String, Object> getDefaultConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // Output settings
        config.put("outputDirectory", "src/test/java/stepdefinitions");
        config.put("basePackage", "com.testautomation.stepdefinitions");
        
        // Generation settings
        config.put("generateWebSteps", true);
        config.put("generateApiSteps", true);
        config.put("generateMobileSteps", true);
        config.put("generateCommonSteps", true);
        
        // Organization settings
        config.put("organizeByStepType", true);
        config.put("organizeByFeature", false);
        config.put("namingConvention", "CAMEL_CASE");
        
        // Report settings
        config.put("generateReports", true);
        config.put("reportFormats", new String[]{"HTML", "JSON", "XML"});
        
        // Validation settings
        config.put("validateSteps", true);
        config.put("excludedSteps", new String[]{});
        
        // Template settings
        config.put("templateDirectory", "templates");
        config.put("customTemplates", new HashMap<String, String>());
        
        return config;
    }
    
    /**
     * Create sample configuration files
     */
    public void createSampleConfigs(String outputDir) throws IOException {
        File dir = new File(outputDir);
        dir.mkdirs();
        
        // Create JSON config
        Map<String, Object> config = getDefaultConfig();
        saveToJson(config, outputDir + "/agent-config.json");
        
        // Create YAML config
        saveToYaml(config, outputDir + "/agent-config.yaml");
        
        logger.info("Sample configuration files created in: {}", outputDir);
    }
}
