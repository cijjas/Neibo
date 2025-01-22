package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURIReferenceInReviewConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURIReferenceInReviewValidator implements ConstraintValidator<UserURIReferenceInReviewConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserURIReferenceInReviewConstraint userURIReferenceInReviewConstraint) {
    }

    @Override
    public boolean isValid(String userURI, ConstraintValidatorContext constraintValidatorContext) {
        if (userURI == null)
            return true;
        if (!formAccessControlHelper.canReferenceUserInAttendance(userURI)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
