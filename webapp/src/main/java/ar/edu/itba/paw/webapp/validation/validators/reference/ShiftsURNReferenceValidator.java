package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ShiftsURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class ShiftsURNReferenceValidator implements ConstraintValidator<ShiftsURNReferenceConstraint, List<String>> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftsURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> shiftURNs, ConstraintValidatorContext context) {
        if (shiftURNs == null)
            return true;
        for (String urn : shiftURNs)
            if (!shiftService.findShift(extractId(urn)).isPresent())
                return false;
        return true;
    }
}
