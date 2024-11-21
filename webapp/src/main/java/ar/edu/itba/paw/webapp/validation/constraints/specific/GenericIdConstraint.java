package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.validators.specific.GenericIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = GenericIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericIdConstraint {
    String message() default "Invalid ID in Path";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
