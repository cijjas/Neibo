package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.ProductStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ProductStatusURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ProductStatusURIValidator implements ConstraintValidator<ProductStatusURIConstraint, String> {

    @Override
    public void initialize(ProductStatusURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productStatusURI, ConstraintValidatorContext context) {
        if (productStatusURI == null)
            return true;
        if (!URIValidator.validateURI(productStatusURI, Endpoint.PRODUCT_STATUSES))
            return false;
        try {
            ProductStatus.fromId(extractFirstId(productStatusURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
