package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.ProductURIInRequestConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductURIInRequestValidator implements ConstraintValidator<ProductURIInRequestConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(ProductURIInRequestConstraint productURIInRequestConstraint) {
    }

    @Override

    public boolean isValid(String productURI, ConstraintValidatorContext constraintValidatorContext) {
        if (productURI == null)
            return true;
        if (!formAccessControlHelper.canReferenceProductInRequest(productURI)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}