package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.EventService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.TwoId;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.EventURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractTwoId;

public class EventURNValidator implements ConstraintValidator<EventURNConstraint, String> {

    @Autowired
    private EventService eventService;

    @Override
    public void initialize(EventURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String eventURN, ConstraintValidatorContext context) {
        if (eventURN == null)
            return true;
        if (!URNValidator.validateURN(eventURN, "event"))
            return false;
        TwoId twoId = extractTwoId(eventURN);
        return eventService.findEvent(twoId.getFirstId(), twoId.getSecondId()).isPresent();
    }
}
