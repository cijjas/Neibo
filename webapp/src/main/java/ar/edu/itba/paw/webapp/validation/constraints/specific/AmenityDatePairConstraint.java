package ar.edu.itba.paw.webapp.validation.constraints.specific;

import ar.edu.itba.paw.webapp.validation.validators.specific.AmenityDatePairValidator;
import ar.edu.itba.paw.webapp.validation.validators.specific.UserTransactionPairValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = AmenityDatePairValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AmenityDatePairConstraint {
    String message() default "Either both amenity and date have to be specified or neither of them";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
