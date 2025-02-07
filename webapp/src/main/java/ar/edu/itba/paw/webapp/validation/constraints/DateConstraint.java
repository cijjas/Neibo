package ar.edu.itba.paw.webapp.validation.constraints;


import ar.edu.itba.paw.webapp.validation.validators.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {
    String message() default "Invalid Date Formatting, yyyy:MM:dd is expected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
