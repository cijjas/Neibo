package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.interfaces.services.ShiftService;
import ar.edu.itba.paw.webapp.form.ReservationTimeForm;
import ar.edu.itba.paw.webapp.form.validation.constraints.ReservationTimeConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ShiftsConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class ShiftsValidator implements ConstraintValidator<ShiftsConstraint, List<Long>> {
    @Autowired
    ShiftService shiftService;

    @Override
    public void initialize(ShiftsConstraint constraint) {
    }

    @Override
    public boolean isValid(List<Long> shifts, ConstraintValidatorContext context) {
        if(shifts == null) {
            //null handled by another validator
            return true;
        }
        if(shifts.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Shift ids must be sent as a comma separated list in between brackets []")
                    .addConstraintViolation();
            return false;
        }
        for(Long shift : shifts) {
            if(!shiftService.findShift(shift).isPresent()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Shifts do not exist")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
