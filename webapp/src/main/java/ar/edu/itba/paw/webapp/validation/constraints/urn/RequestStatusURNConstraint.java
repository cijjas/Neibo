package ar.edu.itba.paw.webapp.validation.constraints.urn;

import ar.edu.itba.paw.webapp.validation.validators.urn.RequestStatusURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = RequestStatusURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestStatusURNConstraint {
    String message() default "Invalid URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
