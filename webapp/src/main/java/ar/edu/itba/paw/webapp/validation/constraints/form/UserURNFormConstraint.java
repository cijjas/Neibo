package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.form.UserURNFormValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserURNFormValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserURNFormConstraint {
    String message() default "Malformed URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
