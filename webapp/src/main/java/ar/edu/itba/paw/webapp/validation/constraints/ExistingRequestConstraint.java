package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ExistingRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ExistingRequestValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingRequestConstraint {
    String message() default "Request does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
