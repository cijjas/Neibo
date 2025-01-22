package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.WorkerURIReferenceInAffiliationConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerURIInAffiliationFormValidator implements ConstraintValidator<WorkerURIReferenceInAffiliationConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(WorkerURIReferenceInAffiliationConstraint workerURIReferenceInAffiliationConstraint) {
    }

    @Override
    public boolean isValid(String workerURI, ConstraintValidatorContext constraintValidatorContext) {
        if (workerURI == null)
            return true;
        if (!formAccessControlHelper.canReferenceWorkerInAffiliation(workerURI)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
