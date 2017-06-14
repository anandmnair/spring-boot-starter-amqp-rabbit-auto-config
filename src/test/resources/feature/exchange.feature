@UnitTest
Feature: Creating amqp exchnage from the configuration by provinding either exact exchange configurations or by providing the global exchage configuration for the default values

  Scenario: 
    Create amqp exchange depending only on each individual configuration and there is no global exchange configuration available

    Given There is no global exchange configuration available
    And These are the exchange configurations available
      | name       | exchangeType | durable | autoDelete | internal | delayed |
      | exchange-1 |              |         |            |          |         |
      | exchange-2 | DIRECT       | true    | false      | false    | false   |
      | exchange-3 | TOPIC        | false   | true       | false    | false   |
      | exchange-4 | FANOUT       | true    | false      | true     | false   |
      | exchange-5 | HEADERS      | true    | false      | false    | true    |
    When I build the exchanges
    Then These are the exchanges created in the system
      | name       | type    | durable | autoDelete | internal | delayed |
      | exchange-1 | topic   | false   | false      | false    | false   |
      | exchange-2 | direct  | true    | false      | false    | false   |
      | exchange-3 | topic   | false   | true       | false    | false   |
      | exchange-4 | fanout  | true    | false      | true     | false   |
      | exchange-5 | headers | true    | false      | false    | true    |

  Scenario: 
    Create amqp exchange depending on each individual configuration and global exchange configuration

    Given Below is the global exchange configuration available
      | name | exchangeType | durable | autoDelete | internal | delayed |
      |      | DIRECT       | false   | false      | false    | false   |
    And These are the exchange configurations available
      | name       | exchangeType | durable | autoDelete | internal | delayed |
      | exchange-1 |              |         |            |          |         |
      | exchange-2 | DIRECT       | true    | false      | false    | false   |
      | exchange-3 | TOPIC        | false   | true       | false    | false   |
      | exchange-4 | FANOUT       | true    | false      | true     | false   |
      | exchange-5 | HEADERS      | true    | false      | false    | true    |
    When I build the exchanges
    Then These are the exchanges created in the system
      | name       | type    | durable | autoDelete | internal | delayed |
      | exchange-1 | direct  | false   | false      | false    | false   |
      | exchange-2 | direct  | true    | false      | false    | false   |
      | exchange-3 | topic   | false   | true       | false    | false   |
      | exchange-4 | fanout  | true    | false      | true     | false   |
      | exchange-5 | headers | true    | false      | false    | true    |

  Scenario: 
    Validate the given amqp exchange configuration

    Given These are the exchange configurations available
      | name       | exchangeType | durable | autoDelete | internal | delayed |
      | exchange-1 |              |         |            |          |         |
      |            | DIRECT       | true    | false      | false    | false   |
      | exchange-3 | TOPIC        | false   | true       | false    | false   |
      | exchange-4 | FANOUT       | true    | false      | true     | false   |
      | exchange-5 | HEADERS      | true    | false      | false    | true    |
    When I validate the exchanges
    Then These are the validation result for the exchanges
      | name       | valid |
      | exchange-1 | true  |
      |            | false |
      | exchange-3 | true  |
      | exchange-4 | true  |
      | exchange-5 | true  |
