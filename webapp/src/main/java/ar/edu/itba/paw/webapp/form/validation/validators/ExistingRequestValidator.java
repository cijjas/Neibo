package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingRequestConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingRequestValidator implements ConstraintValidator<ExistingRequestConstraint, Long> {
    @Autowired
    RequestService requestService;

    @Override
    public void initialize(ExistingRequestConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return requestService.findRequest(id).isPresent();
    }

}
