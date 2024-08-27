package ar.edu.itba.paw.webapp.form.validation.constraints;


import ar.edu.itba.paw.webapp.form.validation.validators.ExistingPostValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ExistingPostValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingPostConstraint {
    String message() default "Post does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
