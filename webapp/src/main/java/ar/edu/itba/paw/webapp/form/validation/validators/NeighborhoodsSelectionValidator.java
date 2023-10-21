package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;


public class NeighborhoodsSelectionValidator implements ConstraintValidator<NeighborhoodsConstraint, List<Long>> {
    @Autowired
    NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodsConstraint neighborhoodConstraint) {

    }

    @Override
    public boolean isValid(List<Long> ids, ConstraintValidatorContext constraintValidatorContext) {
        if(ids == null)
            return false;

        List<Neighborhood> neighborhoods = neighborhoodService.getNeighborhoods();

        for(Long id : ids) {
            int found = 0;
            for (Neighborhood neighborhood : neighborhoods) {
                if (neighborhood.getNeighborhoodId() == id) {
                    found = 1;
                    break;
                }
            }
            if(found == 0) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid neighborhood")
                        .addConstraintViolation();
                return false;
            }
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid neighborhood")
                .addConstraintViolation();
        return false;
    }

}
