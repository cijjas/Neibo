package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ShiftURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ShiftURNValidator implements ConstraintValidator<ShiftURNConstraint, String> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftURNConstraint shiftURNConstraint) {
    }

    @Override
    public boolean isValid(String shiftURN, ConstraintValidatorContext constraintValidatorContext) {
        if (shiftURN == null)
            return true;
        if (!URNValidator.validateURN(shiftURN, "shifts"))
            return false;
        return shiftService.findShift(extractFirstId(shiftURN)).isPresent();
    }
}
