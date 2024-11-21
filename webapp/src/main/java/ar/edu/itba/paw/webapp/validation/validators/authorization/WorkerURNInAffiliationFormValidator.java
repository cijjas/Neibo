package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerURNReferenceInAffiliationConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURNInAffiliationFormValidator implements ConstraintValidator<WorkerURNReferenceInAffiliationConstraint, String> {

    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(WorkerURNReferenceInAffiliationConstraint workerURNInAffiliationFormConstraint) {}

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext constraintValidatorContext) {
        if (workerURN == null)
            return true;
        return accessControlHelper.canCreateAffiliation(workerURN);
    }
}
