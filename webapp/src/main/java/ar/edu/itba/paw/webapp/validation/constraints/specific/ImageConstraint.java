package ar.edu.itba.paw.webapp.validation.constraints.specific;


import ar.edu.itba.paw.webapp.validation.validators.specific.ImageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageConstraint {
    String message() default "Invalid image format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
