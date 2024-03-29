package ar.edu.itba.paw.webapp.form.validation.constraints;


import ar.edu.itba.paw.webapp.form.validation.validators.AmenityURNValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.DateAfterValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.DepartmentValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AmenityURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AmenityURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}