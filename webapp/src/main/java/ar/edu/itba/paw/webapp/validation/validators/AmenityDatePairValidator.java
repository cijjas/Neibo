package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.dto.queryForms.ShiftParams;
import ar.edu.itba.paw.webapp.validation.constraints.AmenityDatePairConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AmenityDatePairValidator implements ConstraintValidator<AmenityDatePairConstraint, ShiftParams> {

    @Override
    public void initialize(AmenityDatePairConstraint constraint) {
    }

    @Override
    public boolean isValid(ShiftParams form, ConstraintValidatorContext context) {
        boolean hasAmenity = form.getAmenity() != null && !form.getAmenity().isEmpty();
        boolean hasDate = form.getDate() != null && !form.getDate().isEmpty();

        return (hasAmenity || !hasDate);
    }
}

