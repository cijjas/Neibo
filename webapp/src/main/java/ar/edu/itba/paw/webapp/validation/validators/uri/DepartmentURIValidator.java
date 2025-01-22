package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.DepartmentService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.DepartmentURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class DepartmentURIValidator implements ConstraintValidator<DepartmentURIConstraint, String> {

    @Autowired
    private DepartmentService departmentService;

    @Override
    public void initialize(DepartmentURIConstraint departmentURIConstraint) {
    }

    @Override
    public boolean isValid(String departmentURI, ConstraintValidatorContext constraintValidatorContext) {
        if (departmentURI == null)
            return true;
        if (!URIValidator.validateURI(departmentURI, Endpoint.DEPARTMENTS))
            return false;
        return departmentService.findDepartment(extractFirstId(departmentURI)).isPresent();
    }
}