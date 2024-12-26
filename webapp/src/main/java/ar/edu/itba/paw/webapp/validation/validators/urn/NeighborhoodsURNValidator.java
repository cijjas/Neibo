package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.NeighborhoodsURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class NeighborhoodsURNValidator implements ConstraintValidator<NeighborhoodsURNConstraint, List<String>> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodsURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> neighborhoodURNs, ConstraintValidatorContext context) {
        if (neighborhoodURNs == null)
            return true;
        for (String neighborhoodURN : neighborhoodURNs) {
            if (!URNValidator.validateURN(neighborhoodURN, "neighborhood"))
                return false;
            if (!neighborhoodService.findNeighborhood(extractFirstId(neighborhoodURN)).isPresent())
                return false;
        }
        return true;
    }
}
