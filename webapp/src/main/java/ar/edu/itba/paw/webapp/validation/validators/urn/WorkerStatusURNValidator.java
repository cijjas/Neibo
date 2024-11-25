package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerStatusURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class WorkerStatusURNValidator implements ConstraintValidator<WorkerStatusURNConstraint, String> {
    @Override
    public void initialize(WorkerStatusURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String workerStatusURN, ConstraintValidatorContext context) {
        if (workerStatusURN == null)
            return true;
        if (!URNValidator.validateURN(workerStatusURN, "worker-status"))
            return false;
        try {
            WorkerStatus.fromId(extractFirstId(workerStatusURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
