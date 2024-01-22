package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ExistingAmenityValidator implements ConstraintValidator<ExistingAmenityConstraint, Long> {
    @Autowired
    AmenityService amenityService;

    @Override
    public void initialize(ExistingAmenityConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return amenityService.findAmenity(id).isPresent();
    }

}
