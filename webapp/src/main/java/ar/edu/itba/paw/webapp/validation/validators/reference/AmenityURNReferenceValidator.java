package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.AmenityURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class AmenityURNReferenceValidator implements ConstraintValidator<AmenityURNReferenceConstraint, String> {

    @Autowired
    private AmenityService amenityService;

    @Override
    public void initialize(AmenityURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String amenityURN, ConstraintValidatorContext context) {
        if (amenityURN == null || amenityURN.trim().isEmpty())
            return true;
        TwoId twoId = extractTwoId(amenityURN);
        return amenityService.findAmenity(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
