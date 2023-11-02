package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.DuplicateEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = DuplicateEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailConstraint {
    String message() default "Email already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
