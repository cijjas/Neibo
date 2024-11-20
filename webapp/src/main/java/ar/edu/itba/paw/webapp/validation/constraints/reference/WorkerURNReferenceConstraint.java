package ar.edu.itba.paw.webapp.validation.constraints.reference;

import ar.edu.itba.paw.webapp.validation.validators.reference.WorkerURNReferenceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = WorkerURNReferenceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkerURNReferenceConstraint {
    String message() default "Invalid URN Reference";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
