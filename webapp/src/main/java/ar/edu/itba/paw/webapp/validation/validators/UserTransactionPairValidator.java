package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.webapp.dto.queryForms.RequestParams;
import ar.edu.itba.paw.webapp.validation.constraints.UserTransactionPairConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserTransactionPairValidator implements ConstraintValidator<UserTransactionPairConstraint, RequestParams> {

    @Override
    public void initialize(UserTransactionPairConstraint constraint) {
    }

    @Override
    public boolean isValid(RequestParams form, ConstraintValidatorContext context) {
        boolean hasUser = form.getUser() != null && !form.getUser().isEmpty();
        boolean hasType = form.getTransactionType() != null && !form.getTransactionType().isEmpty();

        return (hasUser && hasType) || (!hasUser && !hasType);
    }
}

