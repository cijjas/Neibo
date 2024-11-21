package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProductURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class ProductURNReferenceValidator implements ConstraintValidator<ProductURNReferenceConstraint, String> {

    @Autowired
    private ProductService productService;

    @Override
    public void initialize(ProductURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String productURN, ConstraintValidatorContext context) {
        if (productURN == null)
            return true;
        TwoId twoId = extractTwoId(productURN);
        return productService.findProduct(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
