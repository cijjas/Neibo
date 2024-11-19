package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.ChannelURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChannelURNValidator implements ConstraintValidator<ChannelURNConstraint, String> {

    @Override
    public void initialize(ChannelURNConstraint channelURNConstraint) {
    }

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext constraintValidatorContext) {
        return URNValidator.validateURN(channelURN, "channel");

    }
}