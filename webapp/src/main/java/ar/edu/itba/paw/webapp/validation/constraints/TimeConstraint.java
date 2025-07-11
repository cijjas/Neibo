package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.TimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeConstraint {
    String message() default "Invalid Time Formatting, HH:mm:ss is expected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
