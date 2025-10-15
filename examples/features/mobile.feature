Feature: Mobile App Navigation
  As a mobile user
  I want to navigate through the mobile application
  So that I can access different features

  Background:
    Given the mobile app is installed
    And the app is launched
    And I am on the home screen

  Scenario: Navigate to profile screen
    When I tap the profile icon
    Then I should see the profile screen
    And I should see my profile information

  Scenario: Search for products
    When I tap the search icon
    And I enter "laptop" in the search field
    And I tap the search button
    Then I should see search results
    And I should see at least 5 products

  Scenario: Add item to cart
    Given I am on the product details screen
    When I tap the "Add to Cart" button
    Then I should see the success message "Item added to cart"
    And the cart icon should show "1" items

  Scenario: Swipe through product images
    Given I am on the product details screen
    When I swipe left on the product image
    Then I should see the next product image
    When I swipe right on the product image
    Then I should see the previous product image

  Scenario: Scroll through product list
    Given I am on the products screen
    When I scroll down
    Then I should see more products
    And I should see the loading indicator
    When I scroll to the bottom
    Then I should see "Load More" button

  Scenario: Filter products by category
    Given I am on the products screen
    When I tap the filter button
    And I select "Electronics" category
    And I tap the apply button
    Then I should see only electronics products
    And the filter should show "Electronics" as selected

  Scenario: Logout from mobile app
    Given I am logged in to the mobile app
    When I tap the menu button
    And I tap the logout option
    Then I should see the confirmation dialog
    When I tap the confirm button
    Then I should be logged out
    And I should see the login screen
