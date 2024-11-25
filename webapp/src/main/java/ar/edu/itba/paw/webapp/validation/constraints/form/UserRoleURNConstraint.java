package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.urn.UserRoleURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserRoleURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRoleURNConstraint {
    String message() default "Invalid URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}