@UnitTest
Feature: Creating amqp queue from the configuration by provinding either exact queue configurations or by providing the global exchage configuration for the default values

  Scenario: 
    Create amqp queue depending only on each individual configuration and there is no global queue configuration available

    Given There is no global queue configuration available
    And These are the queue configurations available
      | name    | durable | autoDelete | exclusive | deadLetterEnabled |
      | queue-1 |         |            |           |                   |
      | queue-2 | true    | false      | false     | false             |
      | queue-3 | false   | true       | false     | false             |
      | queue-4 | true    | false      | true      | false             |
      | queue-5 | true    | false      | false     | true              |
    And The dead letter exchange is "dead-letter-exchange.dlx" and dead letter queue postfix is ".dlq"
    When I build the queues
    Then These are the queues created in the system
      | name    | durable | autoDelete | exclusive |
      | queue-1 | false   | false      | false     |
      | queue-2 | true    | false      | false     |
      | queue-3 | false   | true       | false     |
      | queue-4 | true    | false      | true      |
      | queue-5 | true    | false      | false     |

  Scenario: 
    Create amqp queue depending on each individual configuration and global queue configuration

    Given Below is the global queue configuration available
      | name | durable | autoDelete | exclusive | deadLetterEnabled |
      |      | false   | false      | false     | false             |
    And These are the queue configurations available
      | name    | durable | autoDelete | exclusive | deadLetterEnabled |
      | queue-1 |         |            |           |                   |
      | queue-2 | true    | false      | false     | false             |
      | queue-3 | false   | true       | false     | false             |
      | queue-4 | true    | false      | true      | false             |
      | queue-5 | true    | false      | false     | true              |
    And The dead letter exchange is "dead-letter-exchange.dlx" and dead letter queue postfix is ".dlq"
    When I build the queues
    Then These are the queues created in the system
      | name    | durable | autoDelete | exclusive |
      | queue-1 | false   | false      | false     |
      | queue-2 | true    | false      | false     |
      | queue-3 | false   | true       | false     |
      | queue-4 | true    | false      | true      |
      | queue-5 | true    | false      | false     |

  Scenario: 
    Validate the given amqp queue configuration

    Given These are the queue configurations available
      | name    | durable | autoDelete | exclusive | deadLetterEnabled |
      | queue-1 |         |            |           |                   |
      |         | true    | false      | false     | false             |
      | queue-3 | false   | true       | false     | false             |
      | queue-4 | true    | false      | true      | false             |
      | queue-5 | true    | false      | false     | true              |
    And The dead letter exchange is "dead-letter-exchange.dlx" and dead letter queue postfix is ".dlq"
    When I validate the queues
    Then These are the validation result for the queues
      | name    | valid |
      | queue-1 | true  |
      |         | false |
      | queue-3 | true  |
      | queue-4 | true  |
      | queue-5 | true  |
