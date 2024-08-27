package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.ExistingChannelValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ExistingChannelValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingChannelConstraint {
    String message() default "Channel does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
