package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftsURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ShiftsURNFormValidator implements ConstraintValidator<ShiftsURNFormConstraint, List<String>> {
    @Override
    public void initialize(ShiftsURNFormConstraint shiftsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> shiftsURN, ConstraintValidatorContext constraintValidatorContext) {
        for (String urn : shiftsURN)
            if (!URNValidator.validateURN(urn, "shifts")) return false;
        return true;
    }
}
