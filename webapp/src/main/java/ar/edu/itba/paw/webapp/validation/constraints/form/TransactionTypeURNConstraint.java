package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.form.TransactionTypeURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = TransactionTypeURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionTypeURNConstraint {
    String message() default "Invalid URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
