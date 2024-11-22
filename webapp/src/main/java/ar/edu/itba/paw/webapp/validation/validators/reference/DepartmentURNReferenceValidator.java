package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.constraints.reference.DepartmentURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class DepartmentURNReferenceValidator implements ConstraintValidator<DepartmentURNReferenceConstraint, String> {

    @Override
    public void initialize(DepartmentURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext context) {
        if (departmentURN == null || departmentURN.trim().isEmpty())
            return true;
        try {
            Department.fromId(extractFirstId(departmentURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
