package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.ProductURNInRequestConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURNInRequestValidator implements ConstraintValidator<ProductURNInRequestConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(ProductURNInRequestConstraint productURNInRequestFormConstraint) {
    }

    @Override

    public boolean isValid(String productURN, ConstraintValidatorContext constraintValidatorContext) {
        if (productURN == null)
            return true;
        if (!formAccessControlHelper.canReferenceProductInRequest(productURN)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}