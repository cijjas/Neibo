package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.DepartmentURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DepartmentURNFormValidator implements ConstraintValidator<DepartmentURNFormConstraint, String> {

    @Override
    public void initialize(DepartmentURNFormConstraint departmentURNConstraint) {
    }

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(departmentURN, "departments");
    }
}