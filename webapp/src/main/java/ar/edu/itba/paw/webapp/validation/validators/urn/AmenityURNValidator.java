package ar.edu.itba.paw.webapp.validation.validators.urn;


import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.AmenityURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class AmenityURNValidator implements ConstraintValidator<AmenityURNConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private AmenityService amenityService;

    @Override
    public void initialize(AmenityURNConstraint amenityURNConstraint) {
    }

    @Override
    public boolean isValid(String amenityURN, ConstraintValidatorContext constraintValidatorContext) {
        if (amenityURN == null)
            return true;
        if (!URNValidator.validateURN(amenityURN, Endpoint.AMENITIES))
            return false;
        TwoId twoId = extractTwoId(amenityURN);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return amenityService.findAmenity(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
