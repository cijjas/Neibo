package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ShiftURNFormValidator implements ConstraintValidator<ShiftURNFormConstraint, String> {

    @Override
    public void initialize(ShiftURNFormConstraint shiftURNConstraint) {}

    @Override
    public boolean isValid(String shiftURN, ConstraintValidatorContext constraintValidatorContext) {
        if (shiftURN == null)
            return true;
        return URNValidator.validateURN(shiftURN, "shifts");
    }
}
