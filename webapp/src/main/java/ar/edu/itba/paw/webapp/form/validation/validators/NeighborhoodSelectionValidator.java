package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;


public class NeighborhoodSelectionValidator implements ConstraintValidator<NeighborhoodConstraint, Long> {
    @Autowired
    NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodConstraint neighborhoodConstraint) {
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) {
            //returns true so the invalid neighborhood message isn't displayed, the null will be caught by another validation
            return true;
        }

        List<Neighborhood> neighborhoods = neighborhoodService.getNeighborhoods();
        for (Neighborhood neighborhood : neighborhoods) {
            if (Objects.equals(neighborhood.getNeighborhoodId(), id)) {
                return true;
            }
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid neighborhood")
                .addConstraintViolation();
        return false;
    }

}
