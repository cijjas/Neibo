package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.form.ShiftURNFormValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ShiftURNFormValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftURNFormConstraint {
    String message() default "Malformed URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}