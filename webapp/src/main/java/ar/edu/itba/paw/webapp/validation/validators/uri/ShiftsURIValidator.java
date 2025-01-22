package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ShiftsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

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
        for (String shiftURI : shiftURIs) {
            if (!URIValidator.validateURI(shiftURI, Endpoint.SHIFTS))
                return false;
            if (!shiftService.findShift(extractFirstId(shiftURI)).isPresent())
                return false;
        }
        return true;
    }
}
