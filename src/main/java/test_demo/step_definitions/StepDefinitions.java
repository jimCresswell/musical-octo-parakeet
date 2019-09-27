package test_demo.step_definitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.Steps;

import test_demo.data.Booking;
import test_demo.data.TestData;
import test_demo.interactions.booking.BookingSteps;
import test_demo.interactions.navigation.NavigateToSteps;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are the Cucumber step definitions, which call into the Serenity steps.
 */
public class StepDefinitions {
    @Steps
    NavigateToSteps navigateTo;

    @Steps
    BookingSteps booking;

    @Given("I want to use the booking website")
    public void i_am_using_the_booking_site() {
        navigateTo.bookingHomePage();
    }

    @When("I try to create a (.*) booking")
    public void i_enter_booking_details(String bookingType) {
        Booking bookingData = TestData.getBooking(bookingType);
        booking.tryToCreateBooking(bookingData);
    }

    @Then("that booking is (.*)")
    public void the_booking_is_accepted_or_not(String acceptedOrNot) {
        List<WebElementFacade> bookingList = booking.getBookings();
        // TO DO: get data from list, move functionality to a utilities class.
    }
}
