package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.form.validation.constraints.EmailConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DuplicateEmailValidator implements ConstraintValidator<EmailConstraint, String> {
    @Autowired
    UserService userService;

    @Override
    public void initialize(EmailConstraint emailConstraint) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(userService.findUser(email).isPresent()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Email already in use")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
