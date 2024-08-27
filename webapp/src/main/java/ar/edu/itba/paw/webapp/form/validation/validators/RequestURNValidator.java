package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.RequestURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestURNValidator implements ConstraintValidator<RequestURNConstraint, String> {

    @Override

    public void initialize(RequestURNConstraint requestURNConstraint) {
    }

    @Override

    public boolean isValid(String requestURN, ConstraintValidatorContext constraintValidatorContext) {

        if (requestURN == null)
            return false;

        return URNValidator.validateURN(requestURN, "requests");

    }

}