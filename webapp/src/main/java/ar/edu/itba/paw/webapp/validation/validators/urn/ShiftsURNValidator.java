package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.ShiftsURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class ShiftsURNValidator implements ConstraintValidator<ShiftsURNConstraint, List<String>> {

    @Autowired
    private ShiftService shiftService;

    @Override
    public void initialize(ShiftsURNConstraint shiftsURNConstraint) {
    }

    @Override
    public boolean isValid(List<String> shiftsURN, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println(shiftsURN);
        if (shiftsURN == null)
            return true;
        for (String shiftURN : shiftsURN) {
            if (!URNValidator.validateURN(shiftURN, "shifts"))
                return false;
            if (!shiftService.findShift(extractFirstId(shiftURN)).isPresent())
                return false;
        }
        return true;
    }
}
