package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURIReferenceInLikeConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURIReferenceInLikeValidator implements ConstraintValidator<UserURIReferenceInLikeConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserURIReferenceInLikeConstraint userURIReferenceInLikeConstraint) {
    }

    @Override
    public boolean isValid(String userURI, ConstraintValidatorContext constraintValidatorContext) {
        if (userURI == null)
            return true;
        if (!formAccessControlHelper.canReferenceUserInLike(userURI)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
