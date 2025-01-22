package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInLikeConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNReferenceInLikeValidator implements ConstraintValidator<UserURNReferenceInLikeConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserURNReferenceInLikeConstraint userURNInLikeFormConstraint) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext constraintValidatorContext) {
        if (userURN == null)
            return true;
        if (!formAccessControlHelper.canReferenceUserInLike(userURN)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
