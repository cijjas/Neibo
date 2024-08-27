package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerRoleURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class WorkerRoleURNValidator implements ConstraintValidator<WorkerRoleURNConstraint, String> {
    @Override
    public void initialize(WorkerRoleURNConstraint workerRoleURNConstraint) {
    }

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        if (workerRoleURN == null)
            return true;
        return URNValidator.validateURN(workerRoleURN, "workerRole");
    }
}