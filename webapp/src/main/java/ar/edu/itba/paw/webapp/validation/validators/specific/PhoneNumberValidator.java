package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.specific.PhoneNumberConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(PhoneNumberConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext context) {
        if (userURN == null)
            return true;
        if (!URNValidator.validateURN(userURN, "users"))
            return false;
        TwoId twoId = extractTwoId(userURN);
        Optional<User> u = userService.findUser(twoId.getFirstId(), twoId.getSecondId());
        return u.filter(user -> user.getPhoneNumber() != null).isPresent();
    }
}
