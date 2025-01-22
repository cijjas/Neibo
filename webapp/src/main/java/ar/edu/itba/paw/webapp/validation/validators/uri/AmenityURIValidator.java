package ar.edu.itba.paw.webapp.validation.validators.uri;


import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.AmenityURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class AmenityURIValidator implements ConstraintValidator<AmenityURIConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private AmenityService amenityService;

    @Override
    public void initialize(AmenityURIConstraint amenityURIConstraint) {
    }

    @Override
    public boolean isValid(String amenityURI, ConstraintValidatorContext constraintValidatorContext) {
        if (amenityURI == null)
            return true;
        if (!URIValidator.validateURI(amenityURI, Endpoint.AMENITIES))
            return false;
        TwoId twoId = extractTwoId(amenityURI);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return amenityService.findAmenity(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
