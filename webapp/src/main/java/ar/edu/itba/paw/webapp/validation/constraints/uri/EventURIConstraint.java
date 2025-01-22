package ar.edu.itba.paw.webapp.validation.constraints.uri;

import ar.edu.itba.paw.webapp.validation.validators.uri.EventURIValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = EventURIValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventURIConstraint {
    String message() default "Invalid URI";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
