package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.RequestStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.RequestStatusURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class RequestStatusURIValidator implements ConstraintValidator<RequestStatusURIConstraint, String> {

    @Override
    public void initialize(RequestStatusURIConstraint requestStatusURIConstraint) {
    }

    @Override
    public boolean isValid(String requestStatusURI, ConstraintValidatorContext constraintValidatorContext) {
        if (requestStatusURI == null)
            return true;
        if (!URIValidator.validateURI(requestStatusURI, Endpoint.REQUEST_STATUSES))
            return false;
        try {
            RequestStatus.fromId(extractFirstId(requestStatusURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }

}