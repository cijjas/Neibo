package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.AmenityDatePairValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = AmenityDatePairValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AmenityDatePairConstraint {
    String message() default "Date query param cannot be used in isolation.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
