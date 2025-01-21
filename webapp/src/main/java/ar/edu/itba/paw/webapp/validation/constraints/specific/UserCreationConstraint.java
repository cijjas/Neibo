package ar.edu.itba.paw.webapp.validation.constraints.specific;

import ar.edu.itba.paw.webapp.validation.validators.specific.UserCreationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserCreationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserCreationConstraint {
    String message() default "Combination of Neighborhood and User Role is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
