Feature: Upsert company exemptions deltas

  Scenario: Consumer publishes a new message onto the company exemptions delta topic
    Given the company exemptions delta consumer service is running
    When the topic receives a message containing a valid CHS delta payload
    Then a PUT request is sent to the company exemptions data api with the transformed data