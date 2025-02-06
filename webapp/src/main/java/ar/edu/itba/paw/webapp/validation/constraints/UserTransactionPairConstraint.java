package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.UserTransactionPairValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserTransactionPairValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserTransactionPairConstraint {
    String message() default "Either both User and Transaction Type have to be specified or none of them";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
