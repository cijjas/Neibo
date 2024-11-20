package ar.edu.itba.paw.webapp.validation.constraints.authorization;


import ar.edu.itba.paw.webapp.validation.validators.authorization.ProductURNInRequestValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ProductURNInRequestValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductURNInRequestConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}