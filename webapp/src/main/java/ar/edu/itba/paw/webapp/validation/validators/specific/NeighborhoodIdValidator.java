package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.enums.BaseNeighborhood;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodIdValidator implements ConstraintValidator<NeighborhoodIdConstraint, Long> {

    @Autowired
    private NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodIdConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long neighborhoodId, ConstraintValidatorContext context) {
        if (neighborhoodId == null)
            return false;
        if (BaseNeighborhood.isABaseNeighborhood(neighborhoodId))
            return false;
        return neighborhoodService.findNeighborhood(neighborhoodId).isPresent();
    }
}
