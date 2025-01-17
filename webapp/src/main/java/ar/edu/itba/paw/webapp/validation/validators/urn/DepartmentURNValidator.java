package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.DepartmentService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.DepartmentURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class DepartmentURNValidator implements ConstraintValidator<DepartmentURNConstraint, String> {

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void initialize(DepartmentURNConstraint departmentURNConstraint) {
    }

    @Override
    public boolean isValid(String departmentURN, ConstraintValidatorContext constraintValidatorContext) {
        if (departmentURN == null)
            return true;
        if (!URNValidator.validateURN(departmentURN, Endpoint.DEPARTMENTS))
            return false;
        return departmentService.findDepartment(extractFirstId(departmentURN)).isPresent();
    }
}