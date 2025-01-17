package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ProductURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class ProductURNValidator implements ConstraintValidator<ProductURNConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private ProductService productService;

    @Override
    public void initialize(ProductURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productURN, ConstraintValidatorContext constraintValidatorContext) {
        if (productURN == null)
            return true;
        if (!URNValidator.validateURN(productURN, Endpoint.PRODUCTS))
            return false;
        TwoId twoId = extractTwoId(productURN);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FORBIDDEN").addConstraintViolation();
            return false;
        }
        return productService.findProduct(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
