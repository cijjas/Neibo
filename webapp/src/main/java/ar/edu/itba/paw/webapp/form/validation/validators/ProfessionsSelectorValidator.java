package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.MainEntities.Neighborhood;
import ar.edu.itba.paw.webapp.form.validation.constraints.NeighborhoodsConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProfessionsConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;


public class ProfessionsSelectorValidator implements ConstraintValidator<ProfessionsConstraint, Long[]> {

    @Override
    public void initialize(ProfessionsConstraint professionsConstraint) {

    }

    @Override
    public boolean isValid(Long[] professions, ConstraintValidatorContext constraintValidatorContext) {
        if(professions == null || professions.length == 0)
            return false;

        for(Long id : professions) {
            int found = 0;
            for (Professions profession : Professions.values()) {
                if (Objects.equals(profession.getId(), id)) {
                    found = 1;
                    break;
                }
            }
            if(found == 0) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid profession")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
