package ar.edu.itba.paw.webapp.form.validation.constraints;


import ar.edu.itba.paw.webapp.form.validation.validators.ImageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageConstraint {
    String message() default "Invalid image format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
