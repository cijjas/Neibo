package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ShiftURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ShiftURIValidator implements ConstraintValidator<ShiftURIConstraint, String> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftURIConstraint shiftURIConstraint) {
    }

    @Override
    public boolean isValid(String shiftURI, ConstraintValidatorContext constraintValidatorContext) {
        if (shiftURI == null)
            return true;
        if (!URIValidator.validateURI(shiftURI, Endpoint.SHIFTS))
            return false;
        return shiftService.findShift(extractFirstId(shiftURI)).isPresent();
    }
}
