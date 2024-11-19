package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ShiftsUpdateURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = ShiftsUpdateURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftsUpdateURNConstraint {
    String message() default "Malformed URNs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}