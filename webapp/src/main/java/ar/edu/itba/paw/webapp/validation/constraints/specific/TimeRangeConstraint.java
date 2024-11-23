package ar.edu.itba.paw.webapp.validation.constraints.specific;

import ar.edu.itba.paw.webapp.validation.validators.specific.TimeRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeRangeConstraint {
    String message() default "Start time must be before end time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
