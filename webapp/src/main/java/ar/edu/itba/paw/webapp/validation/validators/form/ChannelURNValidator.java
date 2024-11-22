package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ChannelURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalTwoId;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class ChannelURNValidator implements ConstraintValidator<ChannelURNConstraint, String> {

    @Autowired
    private ChannelService channelService;

    @Override
    public void initialize(ChannelURNConstraint channelURNConstraint) {}

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext constraintValidatorContext) {
        if (channelURN == null)
            return true;
        if (!URNValidator.validateURN(channelURN, "channel"))
            return false;
        TwoId twoId = extractTwoId(channelURN);
        return channelService.findChannel(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}