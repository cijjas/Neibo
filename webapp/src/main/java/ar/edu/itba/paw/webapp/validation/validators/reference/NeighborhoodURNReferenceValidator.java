package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.NeighborhoodURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class NeighborhoodURNReferenceValidator implements ConstraintValidator<NeighborhoodURNReferenceConstraint, String> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String neighborhoodURN, ConstraintValidatorContext context) {
        if (neighborhoodURN == null || neighborhoodURN.trim().isEmpty())
            return true;
        Long id = extractOptionalFirstId(neighborhoodURN);
        return id == null || neighborhoodService.findNeighborhood(id).isPresent();
    }
}
