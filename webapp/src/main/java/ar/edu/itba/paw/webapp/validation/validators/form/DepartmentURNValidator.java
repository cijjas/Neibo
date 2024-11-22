package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.DepartmentURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractFirstId;

public class DepartmentURNValidator implements ConstraintValidator<DepartmentURNConstraint, String> {

    @Override
    public void initialize(DepartmentURNConstraint departmentURNConstraint) {}

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext constraintValidatorContext) {
        if (departmentURN == null)
            return true;
        if (!URNValidator.validateURN(departmentURN, "departments"))
            return false;
        try {
            Department.fromId(extractFirstId(departmentURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}