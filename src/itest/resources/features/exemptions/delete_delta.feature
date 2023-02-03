Feature: Delete company exemptions deltas

  Scenario: sending DELETE request to data API
    When the topic receives a message with a valid CHS delete delta payload
    Then a DELETE request is sent to the company exemptions data api





