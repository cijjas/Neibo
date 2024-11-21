package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductStatusURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductStatusURNFormValidator implements ConstraintValidator<ProductStatusURNFormConstraint, String> {

    @Override
    public void initialize(ProductStatusURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String productStatusURN, ConstraintValidatorContext context) {
        if (productStatusURN == null)
            return true;
    return URNValidator.validateURN(productStatusURN, "product-status");
    }
}
