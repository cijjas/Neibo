package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerRoleURNReferenceInAffiliationConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerRoleURNReferenceInAffiliationValidator implements ConstraintValidator<WorkerRoleURNReferenceInAffiliationConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(WorkerRoleURNReferenceInAffiliationConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String workerRole, ConstraintValidatorContext constraintValidatorContext) {
        if (workerRole == null)
            return true;
        if (!formAccessControlHelper.canReferenceWorkerRoleInAffiliation(workerRole)){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FORBIDDEN")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
