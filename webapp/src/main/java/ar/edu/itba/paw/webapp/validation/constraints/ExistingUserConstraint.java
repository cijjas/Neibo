package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ExistingUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = ExistingUserValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingUserConstraint {
    String message() default "User does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
