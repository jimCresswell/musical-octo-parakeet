Feature: Using the booking website
  These scenarios operate end-to-end through the UI
  and identify elements by text content because they
  are simulating a user experience.

  Stories tagged @smoke comprise a smoke test of the
  fundamental site functionality.

  Background: The user is visiting the booking site
    This step runs for all scenarios in this feature.
    Given I want to use the booking website

  Scenario Outline: Simple bookings are handled according to validity
    When I try to create a <booking type> booking
    Then that booking is <accepted or not>

    @smoke
    Examples: basic booking validity
      | booking type | accepted or not |
      | valid        | accepted        |
      | invalid      | not accepted    |


  @pending
  Scenario Outline: Other booking scenarios are handled according to validity
    These have been moved into their own scenario outline because Serenity doesn't pick up @pending tags on example groups.
    When I try to create a <booking type> booking
    Then that booking is <accepted or not>

    Examples: other basic cases
    NOTE: @pending tests should be marked as pending by Serenity-Cucumber, there may be a bug where that fails if nested under other tags.
    These cases have not been implemented.
    To implement just create the appropriate test data.
    Don't put a lot of data driven testing in user
    experience specifications, it makes them unreadable,
    move such testing to a lower level e.g. contract testing,
    integration testing or UI components with mocked services.
      | booking type  | accepted or not |
      | no deposit    | accepted        |
      | invalid price | not accepted    |
      | invalid date  | not accepted    |


  @smoke
  Scenario: Existing bookings are displayed on page load
    When there is a booking in the system
    Then that booking displays on page load


  @smoke
  Scenario: Bookings can be deleted
    And there is a booking in the system
    When I delete that booking
    Then the booking is removed from the booking list