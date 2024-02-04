package ar.edu.itba.paw.webapp.form.validation.constraints;


import ar.edu.itba.paw.webapp.form.validation.validators.ReservationDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = ReservationDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReservationDateConstraint {
    String message() default "Invalid Reservation Date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
