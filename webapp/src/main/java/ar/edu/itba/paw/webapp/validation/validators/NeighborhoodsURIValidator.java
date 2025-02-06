package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.NeighborhoodsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NeighborhoodsURIValidator implements ConstraintValidator<NeighborhoodsURIConstraint, List<String>> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodsURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> neighborhoodsURIs, ConstraintValidatorContext context) {
        if (neighborhoodsURIs == null)
            return true;
        for (String neighborhoodURI : neighborhoodsURIs)
            if (!URIValidator.validateOptionalURI(neighborhoodURI, Endpoint.NEIGHBORHOODS))
                return false;
        return true;
    }
}
