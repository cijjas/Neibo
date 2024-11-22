package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.NeighborhoodsURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class NeighborhoodsURNReferenceValidator implements ConstraintValidator<NeighborhoodsURNReferenceConstraint, List<String>> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodsURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> neighborhoodURNs, ConstraintValidatorContext context) {
        if (neighborhoodURNs == null)
            return true;
        for (String neighborhoodURN: neighborhoodURNs) {
            Long id = extractOptionalFirstId(neighborhoodURN);
            if (id != null)
                if (!neighborhoodService.findNeighborhood(id).isPresent())
                    return false;
        }
        return true;
    }
}
