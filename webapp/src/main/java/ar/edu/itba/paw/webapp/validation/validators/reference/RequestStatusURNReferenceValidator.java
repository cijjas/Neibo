package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.RequestStatusURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalFirstId;

public class RequestStatusURNReferenceValidator implements ConstraintValidator<RequestStatusURNReferenceConstraint, String> {

    @Override
    public void initialize(RequestStatusURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String requestStatusURN, ConstraintValidatorContext context) {
        if (requestStatusURN == null || requestStatusURN.trim().isEmpty())
            return true;
        Long requestStatusId = extractOptionalFirstId(requestStatusURN);
        if (requestStatusId == null)
            return true;
        try {
            RequestStatus.fromId(requestStatusId);
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
