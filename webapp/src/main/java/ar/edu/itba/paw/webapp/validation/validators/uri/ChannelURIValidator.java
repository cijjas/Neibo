package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ChannelURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class ChannelURIValidator implements ConstraintValidator<ChannelURIConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private ChannelService channelService;

    @Override
    public void initialize(ChannelURIConstraint channelURIConstraint) {
    }

    @Override
    public boolean isValid(String channelURI, ConstraintValidatorContext constraintValidatorContext) {
        if (channelURI == null)
            return true;
        if (!URIValidator.validateURI(channelURI, Endpoint.CHANNELS))
            return false;
        TwoId twoId = extractTwoId(channelURI);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return channelService.findChannel(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}