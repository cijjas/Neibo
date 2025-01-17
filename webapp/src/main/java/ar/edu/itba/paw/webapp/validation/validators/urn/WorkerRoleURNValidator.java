package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.WorkerRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;


public class WorkerRoleURNValidator implements ConstraintValidator<WorkerRoleURNConstraint, String> {

    @Override
    public void initialize(WorkerRoleURNConstraint workerRoleURNConstraint) {
    }

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        if (workerRoleURN == null)
            return true;
        if (!URNValidator.validateURN(workerRoleURN, Endpoint.WORKER_ROLES))
            return false;
        try {
            WorkerRole.fromId(extractFirstId(workerRoleURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}