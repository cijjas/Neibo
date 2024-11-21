package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.NeighborhoodsURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NeighborhoodsURNFormValidator implements ConstraintValidator<NeighborhoodsURNFormConstraint, List<String>> {

    @Override
    public void initialize(NeighborhoodsURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(List<String> neighborhoodURNs, ConstraintValidatorContext context) {
        if (neighborhoodURNs == null)
            return true;
        for (String neighborhoodURN: neighborhoodURNs)
            if (!URNValidator.validateURN(neighborhoodURN, "neighborhood"))
                return false;
        return true;
    }
}
