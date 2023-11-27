package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;


public class NeighborhoodsSelectionValidator implements ConstraintValidator<NeighborhoodsConstraint, String> {
    @Autowired
    NeighborhoodService neighborhoodService;

    @Override
    public void initialize(NeighborhoodsConstraint neighborhoodConstraint) {

    }

    @Override
    public boolean isValid(String ids, ConstraintValidatorContext constraintValidatorContext) {
        if(ids == null){
            return false;
        }
        //convert the id's string into a List<Long>, where the values are comma separated in the string
        String[] idsString = ids.split(",");
        Long[] idsLong = new Long[idsString.length];
        for(int i = 0; i < idsString.length; i++) {
            idsLong[i] = Long.parseLong(idsString[i]);
        }

        List<Neighborhood> neighborhoods = neighborhoodService.getNeighborhoods();

        for(Long id : idsLong) {
            int found = 0;
            for (Neighborhood neighborhood : neighborhoods) {
                if (Objects.equals(neighborhood.getNeighborhoodId(), id)) {
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
        return true;
    }

}
