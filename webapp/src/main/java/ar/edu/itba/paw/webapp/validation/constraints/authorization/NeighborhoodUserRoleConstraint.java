package ar.edu.itba.paw.webapp.validation.constraints.authorization;


import ar.edu.itba.paw.webapp.validation.validators.authorization.NeighborhoodUserRoleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = NeighborhoodUserRoleValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodUserRoleConstraint {
    String message() default "Invalid combination of User Role and Neighborhood for selected user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
