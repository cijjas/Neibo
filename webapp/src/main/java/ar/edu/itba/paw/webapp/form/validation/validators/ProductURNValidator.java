package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.PostURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProductURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURNValidator implements ConstraintValidator<ProductURNConstraint, String> {

    @Override

    public void initialize(ProductURNConstraint productURNConstraint) {}

    @Override

    public boolean isValid(String productURN, ConstraintValidatorContext constraintValidatorContext) {
        if(productURN==null)
            return false;

        return URNValidator.validateURN(productURN, "product");

    }
}