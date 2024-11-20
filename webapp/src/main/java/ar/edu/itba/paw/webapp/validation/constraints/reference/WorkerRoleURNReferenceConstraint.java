package ar.edu.itba.paw.webapp.validation.constraints.reference;


import ar.edu.itba.paw.webapp.validation.validators.reference.WorkerRoleURNReferenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = WorkerRoleURNReferenceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkerRoleURNReferenceConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
