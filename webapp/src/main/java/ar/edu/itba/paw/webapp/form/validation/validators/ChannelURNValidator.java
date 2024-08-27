package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ChannelURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChannelURNValidator implements ConstraintValidator<ChannelURNConstraint, String> {

    @Override
    public void initialize(ChannelURNConstraint channelURNConstraint) {
    }

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext constraintValidatorContext) {
        if (channelURN == null)
            return false;

        return URNValidator.validateURN(channelURN, "channel");

    }
}