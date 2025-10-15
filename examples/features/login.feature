Feature: User Login
  As a user
  I want to be able to login to the application
  So that I can access my account

  Background:
    Given the application is running
    And I am on the login page

  Scenario: Successful login with valid credentials
    When I enter username "valid@example.com" and password "password123"
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see the welcome message "Welcome, John!"

  Scenario: Failed login with invalid credentials
    When I enter username "invalid@example.com" and password "wrongpassword"
    And I click the login button
    Then I should see the error message "Invalid credentials"
    And I should remain on the login page

  Scenario: Login with empty credentials
    When I leave the username field empty
    And I leave the password field empty
    And I click the login button
    Then I should see the validation error "Username and password are required"

  Scenario Outline: Login with different user roles
    When I enter username "<username>" and password "<password>"
    And I click the login button
    Then I should be redirected to the "<expected_page>"
    And I should see the role-specific message "<role_message>"

    Examples:
      | username              | password    | expected_page | role_message           |
      | admin@example.com      | admin123    | admin-panel  | Welcome, Administrator |
      | user@example.com       | user123     | dashboard    | Welcome, User          |
      | guest@example.com      | guest123    | guest-area   | Welcome, Guest        |
