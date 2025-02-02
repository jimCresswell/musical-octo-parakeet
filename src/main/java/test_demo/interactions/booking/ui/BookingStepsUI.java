package test_demo.interactions.booking.ui;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Step;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import test_demo.data.Booking;
import test_demo.data.TestData;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static test_demo.interactions.booking.ui.BookingResultsPageElements.*;
import static test_demo.interactions.booking.ui.DataInputPageElements.*;

public class BookingStepsUI extends UIInteractionSteps {

    // Save the input first name.
    // The booking ID isn't available to users so
    // test on unique names.
    private String identifyingFirstName = "";

    public String getIdentifyingFirstName() {
        if (identifyingFirstName.isEmpty()) {
            throw new IllegalStateException("Tried to access identifyingFirstName without setting it first.");
        }
        return identifyingFirstName;
    }

    private int bookingWaitInSeconds = 30;

    /**
     * Store the *unique* first name generated by the TestData class.
     * First names won't work is identifiers in general because they
     * won't be unique. The GUI does contain a unique booking ID but
     * this isn't visible to users.
     * @param identifyingFirstName the booking first name.
     */
    public void setIdentifyingFirstName(String identifyingFirstName) {
        this.identifyingFirstName = identifyingFirstName;
    }

    /**
     * Takes a booking type e.g. 'valid', 'invalid', tries to make the booking and returns the booking data.
     *
     * @param bookingType A string description of the booking type, validated in the Booking class.
     * @return the data used to attempt to create a booking.
     */
    @Step
    public Booking tryToCreateBooking(String bookingType) {
        Booking booking = TestData.getBooking(bookingType);

        enterBookingData(booking);

        // Work around. Click somewhere to close any open
        // date-picker dialogues obscuring the save button.
        $(PAGE_HEADER)
                .click();
        $(DATE_PICKER)
                .waitUntilNotVisible();

        $(SAVE_BUTTON)
                .withTimeoutOf(bookingWaitInSeconds, ChronoUnit.SECONDS)
                .waitUntilClickable();

        // Relocating it in case a booking has come into the UI in the meantime and changed the element coordinates.
        $(SAVE_BUTTON)
                .click();

        return booking;
    }

    /**
     * Extract a list of Booking objects from the UI booking data.
     *
     * @return the list of bookings in the UI.
     */
    @Step
    public List<Booking> getBookingsFromUI() {
        List<WebElementFacade> bookingEls = $$(BOOKINGS_LIST);
        return bookingEls
                .stream()
                .map(this::bookingElToList)
                .map(Booking::fromUIData)
                .collect(Collectors.toList());
    }

    /**
     * Given the first name for a booking try to delete that booking.
     * @param identifyingFirstName the first name.
     */
    @Step
    public void tryToDeleteBookingWithFirstName(String identifyingFirstName) {
        By deleteSelector = getBookingDeleteSelectorByFirstName(identifyingFirstName);

        // Wait for the booking to be present in the UI.
        waitForFirstNamePresent(identifyingFirstName);

        // Then try to delete it.
        $(deleteSelector)
                .withTimeoutOf(bookingWaitInSeconds, ChronoUnit.SECONDS)
                .waitUntilClickable()
                .click();
    }

    /**
     * Wait for a booking with a specified first name to turn up.
     * @param identifyingFirstName the first name.
     */
    @Step
    public void waitForFirstNamePresent(String identifyingFirstName) {
        /*
          If the booking hasn't turned up in tens of seconds there
          is a problem whether or not it eventually displays.
        */
        $(getBookingNameSelectorByFirstName(identifyingFirstName))
                .withTimeoutOf(bookingWaitInSeconds, ChronoUnit.SECONDS)
                .waitUntilPresent();
    }

    /**
     * Wait for ten seconds to make sure the booking *isn't* accepted.
     * @param identifyingFirstName
     */
    @Step
    public void waitForFirstNameToFailToAppear(String identifyingFirstName) {
        /*
            I would never normally use Thread.sleep but in the case of
            waiting to make sure something asynchronous *doesn't* happen
            waiting is required.
         */
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        $(getBookingNameSelectorByFirstName(identifyingFirstName))
                .waitUntilNotVisible();
    }

    @Step
    public void waitForFirstNameToDisappear(String identifyingFirstName) {
        $(getBookingNameSelectorByFirstName(identifyingFirstName))
                .withTimeoutOf(bookingWaitInSeconds, ChronoUnit.SECONDS)
                .waitUntilNotVisible();
    }

    /**
     * Given a booking object enter that data into the UI form.
     * @param booking the booking object.
     */
    private void enterBookingData(@NotNull Booking booking) {
        enter(booking.getFirstName()).into(FIRST_NAME);
        enter(booking.getLastName()).into(LAST_NAME);
        enter(booking.getTotalPrice()).into(PRICE);

        $(DEPOSIT).selectByVisibleText(booking.getDepositPaid());

        enter(booking.getCheckIn()).into(CHECK_IN);
        enter(booking.getCheckOut()).into(CHECK_OUT);
    }

    /**
     * Take a webElement containing a booking from the UI and convert it
     * into a list of booking data strings.
     *
     * @param bookingEL the webElement from the UI containing the booking data.
     * @return the list of booking data as strings.
     */
    @NotNull
    private List<String> bookingElToList(@NotNull WebElementFacade bookingEL) {
        String systemId = bookingEL.getAttribute("id");

        List<String> bookingUIData = bookingEL
                .thenFindAll(BOOKING_DATA_ELS)
                .stream()
                .map(WebElementFacade::getTextContent)
                .collect(Collectors.toList());

        bookingUIData.add(0, systemId);

        return bookingUIData;
    }
}
