package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.AmenityURNValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.DepartmentValidator;
import ar.edu.itba.paw.webapp.form.validation.validators.ShiftsURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ShiftsURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShiftsURNConstraint {
    String message() default "Malformed URNs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}