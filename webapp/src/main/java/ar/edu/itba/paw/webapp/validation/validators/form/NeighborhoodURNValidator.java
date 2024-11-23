package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.NeighborhoodURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class NeighborhoodURNValidator implements ConstraintValidator<NeighborhoodURNConstraint, String> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String neighborhoodURN, ConstraintValidatorContext context) {
        if (neighborhoodURN == null)
            return true;
        if (!URNValidator.validateURN(neighborhoodURN, "neighborhood"))
            return false;
        return neighborhoodService.findNeighborhood(extractFirstId(neighborhoodURN)).isPresent();
    }
}
