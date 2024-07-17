package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.ShiftURNValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.UserRoleURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ShiftURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}