package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.validation.constraints.specific.EmailConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DuplicateEmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(EmailConstraint emailConstraint) {}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null)
            return true;

        if (userService.findUser(email).isPresent()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Email already in use")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
