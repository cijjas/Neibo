package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.UserRoleURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;


public class UserRoleURIValidator implements ConstraintValidator<UserRoleURIConstraint, String> {

    @Override
    public void initialize(UserRoleURIConstraint userRoleURIConstraint) {
    }

    @Override
    public boolean isValid(String userRoleURI, ConstraintValidatorContext constraintValidatorContext) {
        if (userRoleURI == null)
            return true;
        if (!URIValidator.validateURI(userRoleURI, Endpoint.USER_ROLES))
            return false;
        try {
            UserRole.fromId(extractFirstId(userRoleURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}