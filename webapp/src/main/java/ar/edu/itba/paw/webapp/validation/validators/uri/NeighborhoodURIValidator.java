package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.NeighborhoodURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class NeighborhoodURIValidator implements ConstraintValidator<NeighborhoodURIConstraint, String> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String neighborhoodURI, ConstraintValidatorContext context) {
        if (neighborhoodURI == null)
            return true;
        if (!URIValidator.validateURI(neighborhoodURI, Endpoint.NEIGHBORHOODS))
            return false;
        return neighborhoodService.findNeighborhood(extractFirstId(neighborhoodURI)).isPresent();
    }
}
