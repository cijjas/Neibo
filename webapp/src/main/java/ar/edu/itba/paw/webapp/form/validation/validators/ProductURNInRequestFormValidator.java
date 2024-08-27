package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProductURNInRequestFormConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURNInRequestFormValidator implements ConstraintValidator<ProductURNInRequestFormConstraint, String> {

    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(ProductURNInRequestFormConstraint productURNInRequestFormConstraint) {
    }

    @Override

    public boolean isValid(String productURN, ConstraintValidatorContext constraintValidatorContext) {
        if (productURN == null)
            return false;

        return URNValidator.validateURN(productURN, "product") && accessControlHelper.canReferenceProductInRequestForm(productURN);

    }
}