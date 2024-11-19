package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.webapp.validation.constraints.DepartmentConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;


public class DepartmentValidator implements ConstraintValidator<DepartmentConstraint, Integer> {

    @Override
    public void initialize(DepartmentConstraint departmentConstraint) {

    }

    @Override
    public boolean isValid(Integer departmentId, ConstraintValidatorContext constraintValidatorContext) {
        if (departmentId == null)
            return true;

        for (Department department : Department.values()) {
            if (Objects.equals(department.getId(), departmentId)) {
                return true;
            }
        }

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid Department Id")
                .addConstraintViolation();
        return false;
    }
}
