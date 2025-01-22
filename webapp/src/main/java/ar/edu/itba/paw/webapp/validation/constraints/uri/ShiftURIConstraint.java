package ar.edu.itba.paw.webapp.validation.constraints.uri;

import ar.edu.itba.paw.webapp.validation.validators.uri.ShiftURIValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ShiftURIValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftURIConstraint {
    String message() default "Invalid URI";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}