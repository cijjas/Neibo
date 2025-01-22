package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.WorkerStatusURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class WorkerStatusURIValidator implements ConstraintValidator<WorkerStatusURIConstraint, String> {
    @Override
    public void initialize(WorkerStatusURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String workersStatusURI, ConstraintValidatorContext context) {
        if (workersStatusURI == null)
            return true;
        if (!URIValidator.validateURI(workersStatusURI, Endpoint.WORKER_STATUSES))
            return false;
        try {
            WorkerStatus.fromId(extractFirstId(workersStatusURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
