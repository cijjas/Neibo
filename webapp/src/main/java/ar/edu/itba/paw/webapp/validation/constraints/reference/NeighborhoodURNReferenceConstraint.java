package ar.edu.itba.paw.webapp.validation.constraints.reference;

import ar.edu.itba.paw.webapp.validation.validators.reference.NeighborhoodURNReferenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = NeighborhoodURNReferenceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodURNReferenceConstraint {
    String message() default "Invalid URN Reference";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
