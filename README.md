# Cucumber Step Definition Generator Agent

A powerful Java agent that automatically generates step definitions from Cucumber feature files, with intelligent duplicate detection, organization, and comprehensive reporting capabilities.

## 🚀 Features

### Core Functionality
- **Automatic Step Definition Generation**: Generates complete step definition methods with proper annotations and method signatures
- **Parameter Extraction**: Intelligently extracts and handles parameters from step text (strings, numbers, data tables, doc strings)
- **Duplicate Detection**: Identifies duplicate steps across multiple feature files and organizes them efficiently
- **Step Type Classification**: Automatically categorizes steps as WEB, API, MOBILE, or COMMON based on content analysis

### Advanced Features
- **Multiple Output Formats**: Generates HTML, JSON, and XML reports
- **Customizable Templates**: Support for custom code templates for different step types
- **Flexible Organization**: Organize steps by type or feature file
- **Naming Convention Support**: CAMEL_CASE, PASCAL_CASE, SNAKE_CASE, KEBAB_CASE
- **Interactive & CLI Modes**: Both interactive and command-line interfaces
- **Comprehensive Validation**: Validates generated code for conflicts and naming issues

### Integration Support
- **Selenium WebDriver**: Ready-to-use web automation templates
- **REST Assured**: API testing templates with request/response handling
- **Appium**: Mobile automation templates for iOS and Android
- **TestNG**: Full TestNG integration with proper annotations
- **Page Object Model**: Generates POM-compatible step definitions

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Cucumber feature files in Gherkin format

## 🛠️ Installation

### Option 1: Clone and Build
```bash
git clone <repository-url>
cd cucumber-step-generator-agent
mvn clean package
```

### Option 2: Use Pre-built JAR
```bash
# Download the latest JAR file
java -jar cucumber-step-generator-agent.jar
```

## 🎯 Quick Start

### Interactive Mode
```bash
java -jar cucumber-step-generator-agent.jar
```

### Command Line Mode
```bash
java -jar cucumber-step-generator-agent.jar src/test/resources/features
```

### With Custom Configuration
```bash
java -jar cucumber-step-generator-agent.jar features \
  --output=src/test/java/stepdefinitions \
  --package=com.company.steps \
  --no-mobile
```

## 📖 Usage Examples

### Basic Usage
```bash
# Generate step definitions for all feature files
java -jar cucumber-step-generator-agent.jar src/test/resources/features
```

### Advanced Configuration
```bash
# Custom output directory and package
java -jar cucumber-step-generator-agent.jar features \
  --output=src/test/java/stepdefinitions \
  --package=com.mycompany.automation.steps

# Skip certain step types
java -jar cucumber-step-generator-agent.jar features \
  --no-mobile \
  --no-api

# Use custom templates
java -jar cucumber-step-generator-agent.jar features \
  --template-dir=custom-templates
```

### Configuration File
Create a `agent-config.yaml` file:
```yaml
outputDirectory: "src/test/java/stepdefinitions"
basePackage: "com.testautomation.stepdefinitions"
generateWebSteps: true
generateApiSteps: true
generateMobileSteps: true
generateCommonSteps: true
organizeByStepType: true
namingConvention: "CAMEL_CASE"
generateReports: true
```

## 📁 Project Structure

```
cucumber-step-generator-agent/
├── src/main/java/com/testautomation/agent/
│   ├── model/                    # Data models
│   │   ├── FeatureStep.java
│   │   ├── StepDefinition.java
│   │   └── GenerationConfig.java
│   ├── parser/                   # Feature file parsing
│   │   └── FeatureFileParser.java
│   ├── generator/                # Step definition generation
│   │   └── StepDefinitionGenerator.java
│   ├── utils/                    # Utility classes
│   │   ├── CodeTemplateManager.java
│   │   ├── NamingUtils.java
│   │   ├── DuplicateStepHandler.java
│   │   └── ReportGenerator.java
│   ├── config/                   # Configuration management
│   │   └── AgentConfig.java
│   └── StepDefinitionGeneratorAgent.java  # Main agent class
├── src/main/resources/
│   ├── templates/                # Code templates
│   │   ├── web.template
│   │   ├── api.template
│   │   └── mobile.template
│   └── agent-config.yaml         # Default configuration
└── pom.xml                      # Maven configuration
```

## 🔧 Configuration Options

### Command Line Options
- `--output=<dir>`: Output directory for step definitions
- `--package=<package>`: Base package name
- `--template-dir=<dir>`: Custom templates directory
- `--no-web`: Skip web step definitions
- `--no-api`: Skip API step definitions
- `--no-mobile`: Skip mobile step definitions
- `--no-common`: Skip common step definitions
- `--no-reports`: Skip report generation
- `--help`: Show help message

### Configuration File Options
```yaml
# Output Settings
outputDirectory: "src/test/java/stepdefinitions"
basePackage: "com.testautomation.stepdefinitions"

# Generation Settings
generateWebSteps: true
generateApiSteps: true
generateMobileSteps: true
generateCommonSteps: true

# Organization Settings
organizeByStepType: true
organizeByFeature: false
namingConvention: "CAMEL_CASE"

# Report Settings
generateReports: true
reportFormats: ["HTML", "JSON", "XML"]

# Validation Settings
validateSteps: true
excludedSteps: []

# Template Settings
templateDirectory: "src/main/resources/templates"
customTemplates: {}
```

## 📊 Generated Output

### Step Definition Files
The agent generates organized step definition files:

```
src/test/java/stepdefinitions/
├── common/
│   └── CommonStepDefinitions.java
├── web/
│   └── WebStepDefinitions.java
├── api/
│   └── ApiStepDefinitions.java
├── mobile/
│   └── MobileStepDefinitions.java
└── reports/
    ├── step_definition_report_20231201_143022.html
    ├── step_definition_report_20231201_143022.json
    └── step_definition_report_20231201_143022.xml
```

### Example Generated Code
```java
package com.testautomation.stepdefinitions.web;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.testng.Assert;

public class WebStepDefinitions {
    private WebDriver driver;
    private WebDriverWait wait;

    public WebStepDefinitions() {
        // Initialize WebDriver
        // this.driver = DriverManager.getDriver();
        // this.wait = new WebDriverWait(driver, 10);
    }

    @Given("I navigate to the login page")
    public void iNavigateToTheLoginPage() {
        // Web automation step implementation
        logger.info("Executing web step: {}", "I navigate to the login page");
        
        try {
            // TODO: Implement web automation logic
            // driver.navigate().to("https://example.com/login");
            logger.info("Navigating to login page");
        } catch (Exception e) {
            logger.error("Error executing web step: {}", e.getMessage());
            throw new RuntimeException("Web step execution failed", e);
        }
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        // Web automation step implementation
        logger.info("Executing web step: {}", "I enter username and password");
        
        try {
            // TODO: Implement web automation logic
            // WebElement usernameField = driver.findElement(By.id("username"));
            // WebElement passwordField = driver.findElement(By.id("password"));
            // usernameField.sendKeys(username);
            // passwordField.sendKeys(password);
            logger.info("Entering username: {} and password", username);
        } catch (Exception e) {
            logger.error("Error executing web step: {}", e.getMessage());
            throw new RuntimeException("Web step execution failed", e);
        }
    }
}
```

## 🔍 Step Type Detection

The agent automatically detects step types based on keywords and content:

### WEB Steps
- Keywords: click, input, enter, verify, assert, navigate, go to, page, element
- Example: "I click the submit button"

### API Steps
- Keywords: api, request, response, get, post, put, delete, status, body
- Example: "I send a GET request to the API"

### MOBILE Steps
- Keywords: mobile, app, device, tap, swipe, scroll, mobile element
- Example: "I tap the login button"

### COMMON Steps
- Keywords: wait, sleep, log, print, setup, teardown
- Example: "I wait for 5 seconds"

## 📈 Reports

### HTML Report
- Comprehensive visual report with tables and statistics
- Step analysis by feature file and type
- Duplicate step identification
- Generation summary

### JSON Report
- Machine-readable format for integration
- Complete step and definition data
- Metadata and timestamps

### XML Report
- Structured format for CI/CD integration
- Step definitions and metadata
- Validation results

## 🎨 Custom Templates

Create custom templates for different step types:

### Template Variables
- `{stepText}`: Original step text
- `{methodName}`: Generated method name
- `{stepType}`: Step type (WEB, API, MOBILE, COMMON)
- `{parameters}`: Step parameters

### Example Custom Template
```java
// Custom web template
logger.info("Executing custom web step: {}", "{stepText}");

// Custom implementation
if ("{stepText}".contains("login")) {
    // Custom login logic
    performCustomLogin();
} else if ("{stepText}".contains("logout")) {
    // Custom logout logic
    performCustomLogout();
}
```

## 🚨 Duplicate Step Handling

The agent intelligently handles duplicate steps:

### Detection
- Identifies steps with identical keywords and text
- Tracks which feature files contain duplicates
- Provides detailed duplicate analysis

### Organization
- Groups common steps into shared files
- Maintains step type separation
- Suggests organization strategies

### Resolution
- Merges duplicate step definitions
- Combines parameters intelligently
- Prevents compilation errors

## 🔧 Integration with Test Frameworks

### TestNG Integration
```java
@BeforeClass
public void setUp() {
    // Initialize dependencies
}

@AfterClass
public void tearDown() {
    // Cleanup resources
}
```

### Page Object Model Support
```java
public class LoginPage {
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "submit")
    private WebElement submitButton;
    
    public void login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        submitButton.click();
    }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **No feature files found**
   - Verify the feature directory path
   - Ensure feature files have `.feature` extension

2. **Duplicate step errors**
   - Use the duplicate detection report
   - Organize steps by type or feature

3. **Template not found**
   - Check template directory path
   - Verify template file extensions

4. **Package naming issues**
   - Use valid Java package naming conventions
   - Avoid reserved keywords

### Debug Mode
```bash
# Enable debug logging
java -jar cucumber-step-generator-agent.jar features --log-level=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Cucumber team for the excellent BDD framework
- Selenium team for web automation capabilities
- REST Assured team for API testing support
- Appium team for mobile automation features

## 📞 Support

For questions, issues, or contributions:
- Create an issue on GitHub
- Check the documentation
- Review the examples and templates

---

**Happy Testing! 🚀**
