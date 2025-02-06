package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.BookingDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookingDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingDateConstraint {
    String message() default "The specified shift is unavailable for the amenity on the chosen date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
