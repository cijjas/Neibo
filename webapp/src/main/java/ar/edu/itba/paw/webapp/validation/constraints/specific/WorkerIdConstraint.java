package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.validators.specific.WorkerIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = WorkerIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkerIdConstraint {
    String message() default "Invalid Worker ID in Path";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
