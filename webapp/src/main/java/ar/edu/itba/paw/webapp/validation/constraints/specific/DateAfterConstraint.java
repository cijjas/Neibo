package ar.edu.itba.paw.webapp.validation.constraints.specific;

import ar.edu.itba.paw.webapp.validation.validators.specific.DateAfterValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateAfterValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateAfterConstraint {
    String message() default "Date must be after today";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
