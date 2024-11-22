package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProductStatusURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class ProductStatusURNReferenceValidator implements ConstraintValidator<ProductStatusURNReferenceConstraint, String> {

    @Override
    public void initialize(ProductStatusURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String productStatusURN, ConstraintValidatorContext context) {
        if (productStatusURN == null)
            return true;
        Long productStatusId = extractOptionalFirstId(productStatusURN);
        if (productStatusId == null)
            return true;
        try {
            ProductStatus.fromId(productStatusId);
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
