package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.dto.queryForms.RequestForm;
import ar.edu.itba.paw.webapp.dto.queryForms.ShiftForm;
import ar.edu.itba.paw.webapp.validation.constraints.specific.AmenityDatePairConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserTransactionPairConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmenityDatePairValidator implements ConstraintValidator<AmenityDatePairConstraint, ShiftForm> {

    @Override
    public void initialize(AmenityDatePairConstraint constraint) {
    }

    @Override
    public boolean isValid(ShiftForm form, ConstraintValidatorContext context) {
        boolean hasAmenity = form.getAmenity() != null && !form.getAmenity().isEmpty();
        boolean hasDate = form.getDate() != null && !form.getDate().isEmpty();

        return (hasAmenity && hasDate) || (!hasAmenity && !hasDate);
    }
}

