package ar.edu.itba.paw.webapp.validation.constraints.authorization;

import ar.edu.itba.paw.webapp.validation.validators.authorization.UserURIReferenceInCreationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserURIReferenceInCreationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserURIReferenceInCreationConstraint {
    String message() default "Unauthorized URI Reference";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}