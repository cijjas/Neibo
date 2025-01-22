package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.WorkerURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class WorkerURIValidator implements ConstraintValidator<WorkerURIConstraint, String> {

    @Autowired
    private WorkerService workerService;

    @Override
    public void initialize(WorkerURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String workerURI, ConstraintValidatorContext context) {
        if (workerURI == null)
            return true;
        if (!URIValidator.validateURI(workerURI, Endpoint.WORKERS))
            return false;
        return workerService.findWorker(extractFirstId(workerURI)).isPresent();
    }
}
