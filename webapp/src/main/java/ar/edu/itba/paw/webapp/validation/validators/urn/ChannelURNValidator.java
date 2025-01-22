package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ChannelURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class ChannelURNValidator implements ConstraintValidator<ChannelURNConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private ChannelService channelService;

    @Override
    public void initialize(ChannelURNConstraint channelURNConstraint) {
    }

    @Override
    public boolean isValid(String channelURN, ConstraintValidatorContext constraintValidatorContext) {
        if (channelURN == null)
            return true;
        if (!URNValidator.validateURN(channelURN, Endpoint.CHANNELS))
            return false;
        TwoId twoId = extractTwoId(channelURN);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return channelService.findChannel(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}