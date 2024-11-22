package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerStatusURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class WorkerStatusURNReferenceValidator implements ConstraintValidator<WorkerStatusURNReferenceConstraint, String> {
    @Override
    public void initialize(WorkerStatusURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerStatusURN, ConstraintValidatorContext context) {
        if (workerStatusURN == null)
            return true;
        try {
            WorkerStatus.fromId(extractFirstId(workerStatusURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
