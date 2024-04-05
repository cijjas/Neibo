package ar.edu.itba.paw.webapp.form.validation.constraints;


import ar.edu.itba.paw.webapp.form.validation.validators.AmenityURNValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.DateAfterValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.DepartmentValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.ProductURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}