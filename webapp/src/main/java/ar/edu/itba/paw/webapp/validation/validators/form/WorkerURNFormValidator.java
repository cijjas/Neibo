package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURNFormValidator implements ConstraintValidator<WorkerURNFormConstraint, String> {
    @Override
    public void initialize(WorkerURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext context) {
        if (workerURN == null)
            return true;
        return URNValidator.validateURN(workerURN, "workers");
    }
}
