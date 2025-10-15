package com.testautomation.agent;

import com.testautomation.agent.model.GenerationConfig;
import com.testautomation.agent.parser.FeatureFileParser;
import com.testautomation.agent.generator.StepDefinitionGenerator;
import com.testautomation.agent.utils.DuplicateStepHandler;
import com.testautomation.agent.utils.ReportGenerator;
import com.testautomation.agent.utils.CodeTemplateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main agent class for generating step definitions from Cucumber feature files
 */
public class StepDefinitionGeneratorAgent {
    private static final Logger logger = LoggerFactory.getLogger(StepDefinitionGeneratorAgent.class);
    
    private final FeatureFileParser parser;
    private final StepDefinitionGenerator generator;
    private final DuplicateStepHandler duplicateHandler;
    private final ReportGenerator reportGenerator;
    private final CodeTemplateManager templateManager;
    
    public StepDefinitionGeneratorAgent() {
        this.parser = new FeatureFileParser();
        this.generator = new StepDefinitionGenerator();
        this.duplicateHandler = new DuplicateStepHandler();
        this.reportGenerator = new ReportGenerator();
        this.templateManager = new CodeTemplateManager();
    }
    
    /**
     * Main method for CLI usage
     */
    public static void main(String[] args) {
        StepDefinitionGeneratorAgent agent = new StepDefinitionGeneratorAgent();
        
        if (args.length == 0) {
            agent.runInteractiveMode();
        } else {
            agent.runCommandLineMode(args);
        }
    }
    
    /**
     * Run in interactive mode
     */
    public void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Cucumber Step Definition Generator Agent ===");
        System.out.println("Welcome! This agent will help you generate step definitions from your feature files.");
        System.out.println();
        
        try {
            // Get feature files directory
            System.out.print("Enter the path to your feature files directory: ");
            String featureDir = scanner.nextLine().trim();
            
            if (!new File(featureDir).exists()) {
                System.err.println("Error: Directory does not exist: " + featureDir);
                return;
            }
            
            // Get output directory
            System.out.print("Enter the output directory for step definitions (default: src/test/java/stepdefinitions): ");
            String outputDir = scanner.nextLine().trim();
            if (outputDir.isEmpty()) {
                outputDir = "src/test/java/stepdefinitions";
            }
            
            // Get base package
            System.out.print("Enter the base package name (default: com.testautomation.stepdefinitions): ");
            String basePackage = scanner.nextLine().trim();
            if (basePackage.isEmpty()) {
                basePackage = "com.testautomation.stepdefinitions";
            }
            
            // Configuration options
            System.out.println("\nConfiguration options:");
            System.out.print("Generate web step definitions? (y/n, default: y): ");
            boolean generateWeb = !scanner.nextLine().trim().toLowerCase().startsWith("n");
            
            System.out.print("Generate API step definitions? (y/n, default: y): ");
            boolean generateApi = !scanner.nextLine().trim().toLowerCase().startsWith("n");
            
            System.out.print("Generate mobile step definitions? (y/n, default: y): ");
            boolean generateMobile = !scanner.nextLine().trim().toLowerCase().startsWith("n");
            
            System.out.print("Generate common step definitions? (y/n, default: y): ");
            boolean generateCommon = !scanner.nextLine().trim().toLowerCase().startsWith("n");
            
            System.out.print("Generate reports? (y/n, default: y): ");
            boolean generateReports = !scanner.nextLine().trim().toLowerCase().startsWith("n");
            
            // Create configuration
            GenerationConfig config = new GenerationConfig();
            config.setOutputDirectory(outputDir);
            config.setBasePackage(basePackage);
            config.setGeneratePageObjectMethods(generateWeb);
            config.setGenerateApiMethods(generateApi);
            config.setGenerateMobileMethods(generateMobile);
            config.setGenerateCommonMethods(generateCommon);
            config.setGenerateReports(generateReports);
            
            // Process feature files
            System.out.println("\nProcessing feature files...");
            this.processFeatureFiles(featureDir, config);
            
            System.out.println("\nStep definition generation completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error during interactive mode", e);
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Run in command line mode
     */
    public void runCommandLineMode(String[] args) {
        try {
            GenerationConfig config = parseCommandLineArgs(args);
            String featureDir = args[0];
            
            System.out.println("=== Cucumber Step Definition Generator Agent ===");
            System.out.println("Processing feature files from: " + featureDir);
            
            processFeatureFiles(featureDir, config);
            
            System.out.println("Step definition generation completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error during command line mode", e);
            System.err.println("Error: " + e.getMessage());
            printUsage();
        }
    }
    
    /**
     * Main processing method
     */
    public void processFeatureFiles(String featureDirectory, GenerationConfig config) throws IOException {
        logger.info("Starting step definition generation process");
        
        // Parse feature files
        logger.info("Parsing feature files from: {}", featureDirectory);
        Map<String, List<com.testautomation.agent.model.FeatureStep>> allSteps = parser.parseFeatureFiles(featureDirectory);
        
        if (allSteps.isEmpty()) {
            logger.warn("No feature files found in directory: {}", featureDirectory);
            System.out.println("No feature files found. Please check the directory path.");
            return;
        }
        
        // Detect duplicates
        logger.info("Detecting duplicate steps");
        Map<com.testautomation.agent.model.FeatureStep, java.util.List<String>> duplicates = 
            duplicateHandler.detectDuplicates(allSteps);
        
        // Organize steps
        logger.info("Organizing steps by type");
        Map<String, java.util.List<com.testautomation.agent.model.FeatureStep>> organizedSteps = 
            duplicateHandler.organizeSteps(allSteps);
        
        // Generate step definitions
        logger.info("Generating step definitions");
        Map<String, java.util.List<com.testautomation.agent.model.StepDefinition>> generatedDefinitions = 
            generator.generateStepDefinitions(allSteps, config);
        
        // Write step definition files
        logger.info("Writing step definition files");
        generator.writeStepDefinitions(generatedDefinitions, config);
        
        // Generate reports
        if (config.isGenerateReports()) {
            logger.info("Generating reports");
            generateReports(allSteps, generatedDefinitions, duplicates, config);
        }
        
        // Display console report
        reportGenerator.generateConsoleReport(allSteps, generatedDefinitions, duplicates);
        
        // Display organization suggestions
        if (!duplicates.isEmpty()) {
            System.out.println("\n" + duplicateHandler.suggestOrganizationStrategy(duplicates));
        }
        
        logger.info("Step definition generation process completed");
    }
    
    /**
     * Generate various reports
     */
    private void generateReports(Map<String, java.util.List<com.testautomation.agent.model.FeatureStep>> allSteps,
                               Map<String, java.util.List<com.testautomation.agent.model.StepDefinition>> generatedDefinitions,
                               Map<com.testautomation.agent.model.FeatureStep, java.util.List<String>> duplicates,
                               GenerationConfig config) throws IOException {
        
        String reportDir = config.getOutputDirectory() + "/reports";
        new File(reportDir).mkdirs();
        
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        // Generate HTML report
        String htmlReportPath = reportDir + "/step_definition_report_" + timestamp + ".html";
        reportGenerator.generateHtmlReport(allSteps, generatedDefinitions, duplicates, htmlReportPath);
        
        // Generate JSON report
        String jsonReportPath = reportDir + "/step_definition_report_" + timestamp + ".json";
        reportGenerator.generateJsonReport(allSteps, generatedDefinitions, duplicates, jsonReportPath);
        
        // Generate XML report
        String xmlReportPath = reportDir + "/step_definition_report_" + timestamp + ".xml";
        reportGenerator.generateXmlReport(allSteps, generatedDefinitions, duplicates, xmlReportPath);
        
        System.out.println("Reports generated:");
        System.out.println("- HTML: " + htmlReportPath);
        System.out.println("- JSON: " + jsonReportPath);
        System.out.println("- XML: " + xmlReportPath);
    }
    
    /**
     * Parse command line arguments
     */
    private GenerationConfig parseCommandLineArgs(String[] args) {
        GenerationConfig config = new GenerationConfig();
        
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            
            if (arg.startsWith("--output=")) {
                config.setOutputDirectory(arg.substring(9));
            } else if (arg.startsWith("--package=")) {
                config.setBasePackage(arg.substring(10));
            } else if (arg.startsWith("--template-dir=")) {
                config.setTemplateDirectory(arg.substring(15));
            } else if (arg.equals("--no-web")) {
                config.setGeneratePageObjectMethods(false);
            } else if (arg.equals("--no-api")) {
                config.setGenerateApiMethods(false);
            } else if (arg.equals("--no-mobile")) {
                config.setGenerateMobileMethods(false);
            } else if (arg.equals("--no-common")) {
                config.setGenerateCommonMethods(false);
            } else if (arg.equals("--no-reports")) {
                config.setGenerateReports(false);
            } else if (arg.equals("--help")) {
                printUsage();
                System.exit(0);
            }
        }
        
        return config;
    }
    
    /**
     * Print usage information
     */
    private void printUsage() {
        System.out.println("Usage: java -jar cucumber-step-generator.jar <feature-directory> [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --output=<dir>           Output directory for step definitions");
        System.out.println("  --package=<package>      Base package name");
        System.out.println("  --template-dir=<dir>     Custom templates directory");
        System.out.println("  --no-web                 Skip web step definitions");
        System.out.println("  --no-api                 Skip API step definitions");
        System.out.println("  --no-mobile              Skip mobile step definitions");
        System.out.println("  --no-common              Skip common step definitions");
        System.out.println("  --no-reports             Skip report generation");
        System.out.println("  --help                   Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar cucumber-step-generator.jar src/test/resources/features");
        System.out.println("  java -jar cucumber-step-generator.jar features --output=stepdefs --package=com.company.steps");
        System.out.println("  java -jar cucumber-step-generator.jar features --no-mobile --no-reports");
    }
    
    /**
     * Validate configuration
     */
    public boolean validateConfiguration(GenerationConfig config) {
        if (config.getOutputDirectory() == null || config.getOutputDirectory().trim().isEmpty()) {
            logger.error("Output directory is required");
            return false;
        }
        
        if (config.getBasePackage() == null || config.getBasePackage().trim().isEmpty()) {
            logger.error("Base package is required");
            return false;
        }
        
        return true;
    }
    
    /**
     * Get agent version
     */
    public String getVersion() {
        return "1.0.0";
    }
    
    /**
     * Get agent capabilities
     */
    public String getCapabilities() {
        return "Cucumber Step Definition Generation Agent\n" +
               "Features:\n" +
               "- Automatic step definition generation\n" +
               "- Duplicate step detection and organization\n" +
               "- Multiple output formats (HTML, JSON, XML)\n" +
               "- Customizable templates\n" +
               "- Support for Web, API, Mobile, and Common steps\n" +
               "- Interactive and command-line modes\n" +
               "- Comprehensive reporting";
    }
}
