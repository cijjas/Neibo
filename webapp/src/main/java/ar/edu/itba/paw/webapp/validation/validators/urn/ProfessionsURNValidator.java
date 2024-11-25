package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ProfessionsURNValidator implements ConstraintValidator<ProfessionsURNConstraint, List<String>> {

    @Autowired
    private ProfessionService professionService;

    @Override
    public void initialize(ProfessionsURNConstraint professionsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> professionURNs, ConstraintValidatorContext constraintValidatorContext) {
        if (professionURNs == null)
            return true;
        for (String professionURN : professionURNs) {
            if (!URNValidator.validateURN(professionURN, "professions"))
                return false;
            if (!professionService.findProfession(extractFirstId(professionURN)).isPresent())
                return false;
        }
        return true;
    }
}