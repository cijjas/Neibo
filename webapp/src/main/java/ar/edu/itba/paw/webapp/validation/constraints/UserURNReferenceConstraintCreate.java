package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.UserURNCreateAuthorizationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserURNCreateAuthorizationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserURNReferenceConstraintCreate {

    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}