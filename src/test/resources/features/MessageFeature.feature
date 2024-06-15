# https://cucumber.io/docs/gherkin/languages/
Feature: Message
  Scenario: Register Message
    When a new message is submitted
    Then the message should be registered with success
    And it should be presented

  Scenario: Get Message
    Given a message was already submitted
    When the message is searched
    Then is successfully presented

  Scenario: Update Message
    Given a message was already submitted
    When an updated request is made
    Then the message is successfully updated

  Scenario: Delete Message
    Given a message was already submitted
    When a remove request is made
    Then the message is successfully deleted
