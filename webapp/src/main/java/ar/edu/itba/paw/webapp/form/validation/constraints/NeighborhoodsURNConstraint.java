package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.NeighborhoodsURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = NeighborhoodsURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodsURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
