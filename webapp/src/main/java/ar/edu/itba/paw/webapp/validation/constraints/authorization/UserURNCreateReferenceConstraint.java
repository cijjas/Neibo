package ar.edu.itba.paw.webapp.validation.constraints.authorization;

import ar.edu.itba.paw.webapp.validation.validators.authorization.UserURNCreateReferenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserURNCreateReferenceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserURNCreateReferenceConstraint {
    String message() default "Unauthorized URN Reference";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}