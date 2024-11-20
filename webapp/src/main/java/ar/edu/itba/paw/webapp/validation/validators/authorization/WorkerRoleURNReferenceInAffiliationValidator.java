package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerRoleURNReferenceInAffiliationConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerRoleURNReferenceInAffiliationValidator implements ConstraintValidator<WorkerRoleURNReferenceInAffiliationConstraint, String> {
    @Autowired
    AccessControlHelper accessControlHelper;

    @Override
    public void initialize(WorkerRoleURNReferenceInAffiliationConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerRole, ConstraintValidatorContext context) {
        System.out.println("Validating Authorization");
        return true;
/*
        if (workerRole == null)
            return true;
        return accessControlHelper.canReferenceWorkerRoleInAffiliationForm(workerRole);
*/
    }
}
