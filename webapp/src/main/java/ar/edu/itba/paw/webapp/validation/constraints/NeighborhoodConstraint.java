package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.NeighborhoodSelectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = NeighborhoodSelectionValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodConstraint {
    String message() default "Invalid neighborhood";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
