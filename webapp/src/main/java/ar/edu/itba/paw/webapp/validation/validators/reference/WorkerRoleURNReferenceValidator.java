package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerRoleURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerRoleURNReferenceValidator implements ConstraintValidator<WorkerRoleURNReferenceConstraint, String> {
    @Override
    public void initialize(WorkerRoleURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext context) {
        System.out.println("Validating Reference");
        return true;
/*
        if (workerRoleURN == null)
            return true;
        Long workerRoleId = extractOptionalId(workerRoleURN);
        try {
            WorkerRole.fromId(workerRoleId);
        } catch (NotFoundException e){
            return false;
        }
        return true;
*/
    }
}
