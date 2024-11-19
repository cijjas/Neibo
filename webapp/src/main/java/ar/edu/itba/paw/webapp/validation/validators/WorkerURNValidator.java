package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.constraints.WorkerURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURNValidator implements ConstraintValidator<WorkerURNConstraint, String> {
    @Override
    public void initialize(WorkerURNConstraint workerURNConstraint) {
    }

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(workerURN, "workers");
    }
}