package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.form.ShiftsURNFormValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ShiftsURNFormValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftsURNFormConstraint {
    String message() default "Malformed URNs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}