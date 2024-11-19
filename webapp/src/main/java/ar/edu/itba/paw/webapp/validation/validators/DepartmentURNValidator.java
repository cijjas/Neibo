package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.DepartmentURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DepartmentURNValidator implements ConstraintValidator<DepartmentURNConstraint, String> {

    @Override
    public void initialize(DepartmentURNConstraint departmentURNConstraint) {
    }

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(departmentURN, "departments");
    }
}