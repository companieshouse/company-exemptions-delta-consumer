Feature: Retries and errors

Scenario: Invalid delta message processed
  When an invalid avro message is sent
  Then the message should be moved to topic company-exemptions-delta-company-exemptions-delta-consumer-invalid

Scenario: Process message with invalid data
  When the consumer receives a message with invalid payload
  Then the message should be moved to topic company-exemptions-delta-company-exemptions-delta-consumer-invalid

Scenario: Process message to correct topic when data api returns 400
  When the consumer receives a message but the data api returns a 400 status code
  Then the message should be moved to topic company-exemptions-delta-company-exemptions-delta-consumer-invalid

Scenario: Process message to correct topic when data api returns 409
  When the consumer receives a message but the data api returns a 409 status code
  Then the message should be moved to topic company-exemptions-delta-company-exemptions-delta-consumer-invalid

Scenario: Process message to correct topic when data api returns 404
  When the consumer receives a message but the data api returns a 404 status code
  Then the message should retry 4 times and then error

Scenario: Process message to correct topic when data api returns 503
  When the consumer receives a message but the data api returns a 503 status code
  Then the message should retry 4 times and then error
