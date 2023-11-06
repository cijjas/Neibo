package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.DepartmentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = DepartmentValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DepartmentConstraint {
    String message() default "Invalid Department";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
