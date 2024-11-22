package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ShiftURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class ShiftURNReferenceValidator implements ConstraintValidator<ShiftURNReferenceConstraint, String> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String shiftURN, ConstraintValidatorContext context) {
        if (shiftURN == null || shiftURN.trim().isEmpty())
            return true;
        return shiftService.findShift(extractFirstId(shiftURN)).isPresent();
    }
}
