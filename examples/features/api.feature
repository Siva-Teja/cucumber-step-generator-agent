Feature: User API Management
  As a developer
  I want to test the user management API
  So that I can ensure proper user operations

  Background:
    Given the API base URL is "https://api.example.com"
    And I have valid API credentials

  Scenario: Create a new user via API
    When I send a POST request to "/users" with the following data:
      """
      {
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "user"
      }
      """
    Then the response status code should be 201
    And the response should contain "user_id"
    And the response should contain "John Doe"

  Scenario: Get user by ID
    Given a user with ID "123" exists
    When I send a GET request to "/users/123"
    Then the response status code should be 200
    And the response should contain "name"
    And the response should contain "email"

  Scenario: Update user information
    Given a user with ID "123" exists
    When I send a PUT request to "/users/123" with the following data:
      """
      {
        "name": "Jane Doe",
        "email": "jane.doe@example.com"
      }
      """
    Then the response status code should be 200
    And the response should contain "Jane Doe"

  Scenario: Delete user
    Given a user with ID "123" exists
    When I send a DELETE request to "/users/123"
    Then the response status code should be 204
    And the user should be deleted

  Scenario: Get all users with pagination
    When I send a GET request to "/users?page=1&limit=10"
    Then the response status code should be 200
    And the response should contain "users"
    And the response should contain "total_count"
    And the response should contain "page"

  Scenario: Search users by email
    When I send a GET request to "/users/search?email=john@example.com"
    Then the response status code should be 200
    And the response should contain "users"
    And each user should have "email" field
