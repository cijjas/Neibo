package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ChannelURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractTwoId;

public class ChannelURNReferenceValidator implements ConstraintValidator<ChannelURNReferenceConstraint, String> {

    @Autowired
    private ChannelService channelService;

    @Override
    public void initialize(ChannelURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext context) {
        if (channelURN == null)
            return true;
        TwoId twoId = extractTwoId(channelURN);
        return channelService.findChannel(twoId.getSecondId(), twoId.getFirstId()).isPresent();
    }
}
