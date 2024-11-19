package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.WorkerRoleURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class WorkerRoleURNValidator implements ConstraintValidator<WorkerRoleURNConstraint, String> {

    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(WorkerRoleURNConstraint workerRoleURNConstraint) {
    }

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(workerRoleURN, "workerRole") && accessControlHelper.canReferenceWorkerRoleInAffiliationForm(workerRoleURN);
    }
}