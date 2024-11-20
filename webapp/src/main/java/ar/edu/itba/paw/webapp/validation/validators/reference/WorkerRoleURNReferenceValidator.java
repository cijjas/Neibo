package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerRoleURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalId;

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
