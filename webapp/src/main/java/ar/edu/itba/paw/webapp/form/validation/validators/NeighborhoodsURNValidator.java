package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsURNConstraint;

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
            return false;
        for (String urn : neighborhoodsURN)
            if (!URNValidator.validateURN(urn, "neighborhood")) return false;
        return true;
    }
}