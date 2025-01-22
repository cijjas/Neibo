package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ProductURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class ProductURIValidator implements ConstraintValidator<ProductURIConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private ProductService productService;

    @Override
    public void initialize(ProductURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productURI, ConstraintValidatorContext constraintValidatorContext) {
        if (productURI == null)
            return true;
        if (!URIValidator.validateURI(productURI, Endpoint.PRODUCTS))
            return false;
        TwoId twoId = extractTwoId(productURI);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return productService.findProduct(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
