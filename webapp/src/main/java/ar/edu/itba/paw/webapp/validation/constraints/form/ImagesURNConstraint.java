package ar.edu.itba.paw.webapp.validation.constraints.form;

import ar.edu.itba.paw.webapp.validation.validators.urn.ImagesURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ImagesURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImagesURNConstraint {
    String message() default "Invalid URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
