package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ProductStatusURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ProductStatusURNValidator implements ConstraintValidator<ProductStatusURNConstraint, String> {

    @Override
    public void initialize(ProductStatusURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productStatusURN, ConstraintValidatorContext context) {
        if (productStatusURN == null)
            return true;
        if (!URNValidator.validateURN(productStatusURN, Endpoint.PRODUCT_STATUSES))
            return false;
        try {
            ProductStatus.fromId(extractFirstId(productStatusURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
