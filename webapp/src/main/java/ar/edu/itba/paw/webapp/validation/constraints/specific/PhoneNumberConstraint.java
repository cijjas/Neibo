package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.validators.specific.GenericIdValidator;
import ar.edu.itba.paw.webapp.validation.validators.specific.PhoneNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberConstraint {
    String message() default "User must have a phone number associated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
