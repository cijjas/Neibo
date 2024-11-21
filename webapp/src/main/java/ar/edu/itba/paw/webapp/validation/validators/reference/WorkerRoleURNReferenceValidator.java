package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerRoleURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class WorkerRoleURNReferenceValidator implements ConstraintValidator<WorkerRoleURNReferenceConstraint, String> {

    @Override
    public void initialize(WorkerRoleURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerRoleURN, ConstraintValidatorContext context) {
        if (workerRoleURN == null || workerRoleURN.trim().isEmpty())
            return true;
        long workerRoleId = extractId(workerRoleURN);
        try {
            WorkerRole.fromId(workerRoleId);
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
