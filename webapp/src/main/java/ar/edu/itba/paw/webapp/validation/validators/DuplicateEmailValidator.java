package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.validation.constraints.EmailConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DuplicateEmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(EmailConstraint emailConstraint) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isEmpty())
            return true;
        return !userService.findUser(email).isPresent();
    }
}
