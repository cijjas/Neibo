package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserURNFormValidator implements ConstraintValidator<UserURNFormConstraint, String> {

    @Override
    public void initialize(UserURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String userURN, ConstraintValidatorContext context) {
        if (userURN == null)
            return true;
        return URNValidator.validateURN(userURN, "users");
    }
}
