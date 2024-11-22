package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class RequestStatusURNValidator implements ConstraintValidator<RequestStatusURNConstraint, String> {

    @Override
    public void initialize(RequestStatusURNConstraint requestStatusURNConstraint) {}

    @Override
    public boolean isValid(String requestStatusURN, ConstraintValidatorContext constraintValidatorContext) {
        if (requestStatusURN == null)
            return true;
        if (!URNValidator.validateURN(requestStatusURN, "requestStatus"))
            return false;
        try {
            RequestStatus.fromId(extractFirstId(requestStatusURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }

}