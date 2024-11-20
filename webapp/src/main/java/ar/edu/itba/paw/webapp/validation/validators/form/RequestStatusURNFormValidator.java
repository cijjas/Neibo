package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestStatusURNFormValidator implements ConstraintValidator<RequestStatusURNFormConstraint, String> {

    @Override
    public void initialize(RequestStatusURNFormConstraint requestStatusURNConstraint) {
    }

    @Override
    public boolean isValid(String requestStatusURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(requestStatusURN, "requestStatus");

    }

}