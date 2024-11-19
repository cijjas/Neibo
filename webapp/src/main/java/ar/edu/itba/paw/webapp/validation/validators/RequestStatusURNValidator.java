package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.RequestStatusURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestStatusURNValidator implements ConstraintValidator<RequestStatusURNConstraint, String> {

    @Override
    public void initialize(RequestStatusURNConstraint requestStatusURNConstraint) {
    }

    @Override
    public boolean isValid(String requestStatusURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(requestStatusURN, "requestStatus");

    }

}