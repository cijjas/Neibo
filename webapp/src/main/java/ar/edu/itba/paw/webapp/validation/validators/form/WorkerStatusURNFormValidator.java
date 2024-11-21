package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerStatusURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerStatusURNFormValidator implements ConstraintValidator<WorkerStatusURNFormConstraint, String> {
    @Override
    public void initialize(WorkerStatusURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerStatusURN, ConstraintValidatorContext context) {
        if (workerStatusURN == null)
            return true;
        return URNValidator.validateURN(workerStatusURN, "worker-status");
    }
}
