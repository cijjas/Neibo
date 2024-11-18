package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.auth.AccessControlHelper;
import ar.edu.itba.paw.webapp.form.validation.constraints.WorkerURNInAffiliationFormConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURNInAffiliationFormValidator implements ConstraintValidator<WorkerURNInAffiliationFormConstraint, String> {
    @Autowired
    private AccessControlHelper accessControlHelper;

    @Override
    public void initialize(WorkerURNInAffiliationFormConstraint workerURNInAffiliationFormConstraint) {
    }

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(workerURN, "workers") && accessControlHelper.canCreateAffiliation(workerURN);
    }
}
