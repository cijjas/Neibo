package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.NeighborhoodsSelectionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Constraint(validatedBy = NeighborhoodsSelectionValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeighborhoodsConstraint {
    String message() default "Invalid neighborhood";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
