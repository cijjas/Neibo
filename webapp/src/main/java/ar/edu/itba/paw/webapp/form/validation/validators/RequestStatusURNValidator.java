package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.RequestStatusURNConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequestStatusURNValidator implements ConstraintValidator<RequestStatusURNConstraint, String> {

    @Override
    public void initialize(RequestStatusURNConstraint requestStatusURNConstraint) {}

    @Override
    public boolean isValid(String requestStatusURN, ConstraintValidatorContext constraintValidatorContext) {

        if(requestStatusURN==null)
            return false;

        return URNValidator.validateURN(requestStatusURN, "requestStatus");

    }

}