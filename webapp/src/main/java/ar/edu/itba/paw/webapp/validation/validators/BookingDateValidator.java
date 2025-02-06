package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.webapp.dto.BookingDto;
import ar.edu.itba.paw.webapp.validation.constraints.BookingDateConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

public class BookingDateValidator implements ConstraintValidator<BookingDateConstraint, BookingDto> {

    @Autowired
    private AvailabilityService availabilityService;

    @Override
    public void initialize(BookingDateConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (bookingDto == null || bookingDto.getAmenity() == null || bookingDto.getBookingDate() == null || bookingDto.getShift() == null || bookingDto.getUser() == null)
            return true;

        // Check Date Format
        LocalDate reservationDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            reservationDate = LocalDate.parse(bookingDto.getBookingDate(), formatter);
        } catch (DateTimeParseException e) {
            context.buildConstraintViolationWithTemplate("Invalid reservation date format. Expected format: yyyy-MM-dd")
                    .addConstraintViolation();
            return true;
        }

        // Check that the reservation date matches the shift's weekday
        DayOfWeek reservationDay = reservationDate.getDayOfWeek();
        String reservationDayName = reservationDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        // PreAuthorize guarantees availability existence
        if (!reservationDayName.equalsIgnoreCase(availabilityService.findAvailability(extractFirstId(bookingDto.getShift()), extractSecondId(bookingDto.getAmenity())).get().getShift().getDay().getDayName())) {
            context.buildConstraintViolationWithTemplate(
                            "The reservation date does not match the shift's available weekday")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
