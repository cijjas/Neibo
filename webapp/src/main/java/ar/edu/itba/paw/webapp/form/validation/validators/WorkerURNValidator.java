package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class WorkerURNValidator implements ConstraintValidator<WorkerURNConstraint, String> {
    @Override
    public void initialize(WorkerURNConstraint workerURNConstraint) {}

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext constraintValidatorContext) {
        if(workerURN==null)
            return false;
        return URNValidator.validateURN(workerURN, "worker");
    }
}