package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalTwoId;

public class UserURNReferenceValidator implements ConstraintValidator<UserURNReferenceConstraint, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UserURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext context) {
        if (userURN == null || userURN.trim().isEmpty())
            return true;
        TwoId twoId = extractOptionalTwoId(userURN);
        return twoId == null || userService.findUser(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
