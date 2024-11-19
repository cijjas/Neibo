package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ShiftsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ShiftsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftsConstraint {
    String message() default "Invalid Shift";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
