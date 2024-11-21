package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURNFormValidator implements ConstraintValidator<ProductURNFormConstraint, String> {

    @Override
    public void initialize(ProductURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String productURN, ConstraintValidatorContext context) {
        if (productURN == null)
            return true;
        return URNValidator.validateURN(productURN, "product");
    }
}
