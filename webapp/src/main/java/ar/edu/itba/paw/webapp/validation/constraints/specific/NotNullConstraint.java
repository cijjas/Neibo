package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.validators.specific.NotNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = NotNullValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullConstraint {
    String message() default "May not be Null";

    Class<?>[] groups() default {Null.class};

    Class<? extends Payload>[] payload() default {};
}
