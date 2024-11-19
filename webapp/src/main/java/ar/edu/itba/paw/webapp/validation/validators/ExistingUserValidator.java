package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.validation.constraints.ExistingUserConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingUserValidator implements ConstraintValidator<ExistingUserConstraint, Long> {
    @Autowired
    UserService userService;

    @Override
    public void initialize(ExistingUserConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return userService.findUser(id).isPresent();
    }

}
