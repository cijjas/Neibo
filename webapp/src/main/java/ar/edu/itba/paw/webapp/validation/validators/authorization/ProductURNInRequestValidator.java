package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.ProductURNInRequestConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURNInRequestValidator implements ConstraintValidator<ProductURNInRequestConstraint, String> {

    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(ProductURNInRequestConstraint productURNInRequestFormConstraint) {}

    @Override

    public boolean isValid(String productURN, ConstraintValidatorContext constraintValidatorContext) {
        if (productURN == null)
            return true;
        return accessControlHelper.canReferenceProductInRequestForm(productURN);
    }
}