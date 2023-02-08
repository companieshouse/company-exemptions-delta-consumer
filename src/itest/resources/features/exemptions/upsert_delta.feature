Feature: Upsert company exemptions deltas

  Scenario: Consumer publishes a new valid upsert message onto the company exemptions delta topic
    When the topic receives a message containing a valid CHS upsert delta payload
    Then a PUT request is sent to the company exemptions data api with the transformed data