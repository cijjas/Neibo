package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ChannelURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChannelURNFormValidator implements ConstraintValidator<ChannelURNFormConstraint, String> {

    @Override
    public void initialize(ChannelURNFormConstraint channelURNConstraint) {}

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext constraintValidatorContext) {
        if (channelURN == null)
            return true;
        return URNValidator.validateURN(channelURN, "channel");
    }
}