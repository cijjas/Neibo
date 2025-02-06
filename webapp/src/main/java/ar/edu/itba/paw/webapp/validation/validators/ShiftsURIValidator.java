package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.ShiftsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ShiftsURIValidator implements ConstraintValidator<ShiftsURIConstraint, List<String>> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftsURIConstraint shiftsURIConstraint) {
    }

    @Override
    public boolean isValid(List<String> shiftURIs, ConstraintValidatorContext constraintValidatorContext) {
        if (shiftURIs == null)
            return true;
        for (String shiftURI : shiftURIs)
            if (!URIValidator.validateOptionalURI(shiftURI, Endpoint.SHIFTS))
                return false;
        return true;
    }
}
