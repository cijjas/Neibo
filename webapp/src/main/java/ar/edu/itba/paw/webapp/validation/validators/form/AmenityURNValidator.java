package ar.edu.itba.paw.webapp.validation.validators.form;


import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.AmenityURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class AmenityURNValidator implements ConstraintValidator<AmenityURNConstraint, String> {

    @Autowired
    private AmenityService amenityService;

    @Override
    public void initialize(AmenityURNConstraint amenityURNConstraint) {}

    @Override
    public boolean isValid(String amenityURN, ConstraintValidatorContext constraintValidatorContext) {
        if (amenityURN == null)
            return true;
        if (!URNValidator.validateURN(amenityURN, "amenity"))
            return false;
        TwoId twoId = extractTwoId(amenityURN);
        return amenityService.findAmenity(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
