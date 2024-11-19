package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ProfessionsSelectorValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = ProfessionsSelectorValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfessionsConstraint {
    String message() default "Invalid profession";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
