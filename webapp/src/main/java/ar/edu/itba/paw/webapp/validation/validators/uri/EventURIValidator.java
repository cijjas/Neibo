package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.EventURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class EventURIValidator implements ConstraintValidator<EventURIConstraint, String> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Autowired
    private EventService eventService;

    @Override
    public void initialize(EventURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String eventURI, ConstraintValidatorContext constraintValidatorContext) {
        if (eventURI == null)
            return true;
        if (!URIValidator.validateURI(eventURI, Endpoint.EVENTS))
            return false;
        TwoId twoId = extractTwoId(eventURI);
        if (!formAccessControlHelper.canReferenceNeighborhoodEntity(twoId.getFirstId())){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return eventService.findEvent(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
