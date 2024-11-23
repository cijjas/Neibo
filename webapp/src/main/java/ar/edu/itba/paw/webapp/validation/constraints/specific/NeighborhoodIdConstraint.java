package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.validators.specific.NeighborhoodIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = NeighborhoodIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodIdConstraint {
    String message() default "Invalid Neighborhood ID in Path";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
