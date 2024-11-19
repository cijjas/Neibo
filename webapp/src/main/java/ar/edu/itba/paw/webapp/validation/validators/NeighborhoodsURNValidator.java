package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.NeighborhoodsURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NeighborhoodsURNValidator implements ConstraintValidator<NeighborhoodsURNConstraint, List<String>> {
    @Override
    public void initialize(NeighborhoodsURNConstraint neighborhoodsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> neighborhoodsURN, ConstraintValidatorContext constraintValidatorContext) {
        if (neighborhoodsURN == null)
            return true;
        for (String urn : neighborhoodsURN)
            if (!URNValidator.validateURN(urn, "neighborhood")) return false;
        return true;
    }
}