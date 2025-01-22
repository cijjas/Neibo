package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.WorkerRoleURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;


public class WorkerRoleURIValidator implements ConstraintValidator<WorkerRoleURIConstraint, String> {

    @Override
    public void initialize(WorkerRoleURIConstraint workerRoleURIConstraint) {
    }

    @Override
    public boolean isValid(String workerRoleURI, ConstraintValidatorContext constraintValidatorContext) {
        if (workerRoleURI == null)
            return true;
        if (!URIValidator.validateURI(workerRoleURI, Endpoint.WORKER_ROLES))
            return false;
        try {
            WorkerRole.fromId(extractFirstId(workerRoleURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}