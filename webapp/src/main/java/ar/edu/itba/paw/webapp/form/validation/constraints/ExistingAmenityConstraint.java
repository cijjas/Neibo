package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.ExistingAmenityValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingAmenityValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingAmenityConstraint {
    String message() default "Amenity does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
