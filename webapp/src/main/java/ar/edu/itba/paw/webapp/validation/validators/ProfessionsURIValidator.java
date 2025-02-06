package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.ProfessionsURIConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class ProfessionsURIValidator implements ConstraintValidator<ProfessionsURIConstraint, List<String>> {

    @Autowired
    private ProfessionService professionService;

    @Override
    public void initialize(ProfessionsURIConstraint professionsURIConstraint) {
    }

    @Override
    public boolean isValid(List<String> professionURIs, ConstraintValidatorContext constraintValidatorContext) {
        if (professionURIs == null)
            return true;
        for (String professionURI : professionURIs)
            if (!URIValidator.validateOptionalURI(professionURI, Endpoint.PROFESSIONS))
                return false;
        return true;
    }
}