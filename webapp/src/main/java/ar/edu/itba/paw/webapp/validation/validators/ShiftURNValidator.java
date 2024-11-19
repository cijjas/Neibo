package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.ShiftURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ShiftURNValidator implements ConstraintValidator<ShiftURNConstraint, String> {
    @Override
    public void initialize(ShiftURNConstraint shiftURNConstraint) {
    }

    @Override
    public boolean isValid(String shiftURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(shiftURN, "shifts");
    }
}
