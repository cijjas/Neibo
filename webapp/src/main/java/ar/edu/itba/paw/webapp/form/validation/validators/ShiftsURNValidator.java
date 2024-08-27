package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ShiftsURNValidator implements ConstraintValidator<ShiftsURNConstraint, List<String>> {
    @Override
    public void initialize(ShiftsURNConstraint shiftsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> shiftsURN, ConstraintValidatorContext constraintValidatorContext) {
        if (shiftsURN == null)
            return true;
        for (String urn : shiftsURN)
            if (!URNValidator.validateURN(urn, "shifts")) return false;
        return true;
    }
}
