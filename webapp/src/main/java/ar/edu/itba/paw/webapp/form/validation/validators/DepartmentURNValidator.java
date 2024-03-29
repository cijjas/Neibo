package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.DepartmentURNConstraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DepartmentURNValidator implements ConstraintValidator<DepartmentURNConstraint, String> {

    @Override
    public void initialize(DepartmentURNConstraint departmentURNConstraint) {}

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext constraintValidatorContext) {
        if(departmentURN==null)
            return false;

        return URNValidator.validateURN(departmentURN, "departments");

    }
}