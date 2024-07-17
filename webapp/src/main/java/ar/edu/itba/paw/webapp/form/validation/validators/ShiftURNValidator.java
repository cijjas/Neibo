package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ShiftURNValidator implements ConstraintValidator<ShiftURNConstraint, String> {
    @Override
    public void initialize(ShiftURNConstraint shiftURNConstraint) {}

    @Override
    public boolean isValid(String shiftURN, ConstraintValidatorContext constraintValidatorContext) {
        if(shiftURN==null)
            return false;
        return URNValidator.validateURN(shiftURN, "shifts");
    }
}
