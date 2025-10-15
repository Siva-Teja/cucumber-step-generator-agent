#!/bin/bash

# Cucumber Step Definition Generator Agent - Usage Example
# This script demonstrates how to use the agent with different configurations

echo "=== Cucumber Step Definition Generator Agent Demo ==="
echo

# Check if JAR file exists
JAR_FILE="target/cucumber-step-generator-agent-1.0.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Building the agent..."
    mvn clean package -DskipTests
    JAR_FILE="target/cucumber-step-generator-agent-1.0.0.jar"
fi

# Example 1: Basic usage with example feature files
echo "Example 1: Basic usage with example feature files"
echo "Command: java -jar $JAR_FILE examples/features"
echo
java -jar "$JAR_FILE" examples/features
echo

# Example 2: Custom output directory and package
echo "Example 2: Custom output directory and package"
echo "Command: java -jar $JAR_FILE examples/features --output=generated-steps --package=com.example.steps"
echo
java -jar "$JAR_FILE" examples/features --output=generated-steps --package=com.example.steps
echo

# Example 3: Skip mobile steps
echo "Example 3: Skip mobile steps"
echo "Command: java -jar $JAR_FILE examples/features --no-mobile"
echo
java -jar "$JAR_FILE" examples/features --no-mobile
echo

# Example 4: Generate only web and API steps
echo "Example 4: Generate only web and API steps"
echo "Command: java -jar $JAR_FILE examples/features --no-mobile --no-common"
echo
java -jar "$JAR_FILE" examples/features --no-mobile --no-common
echo

# Example 5: Use custom templates
echo "Example 5: Use custom templates"
echo "Command: java -jar $JAR_FILE examples/features --template-dir=src/main/resources/templates"
echo
java -jar "$JAR_FILE" examples/features --template-dir=src/main/resources/templates
echo

echo "=== Demo completed ==="
echo "Check the generated step definition files and reports!"
