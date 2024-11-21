package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerRoleURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class WorkerRoleURNFormValidator implements ConstraintValidator<WorkerRoleURNFormConstraint, String> {

    @Override
    public void initialize(WorkerRoleURNFormConstraint workerRoleURNConstraint) {}

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        if (workerRoleURN == null)
            return true;
        return URNValidator.validateURN(workerRoleURN, "workerRole");
    }
}