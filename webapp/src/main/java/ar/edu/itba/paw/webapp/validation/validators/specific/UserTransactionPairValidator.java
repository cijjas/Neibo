package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.dto.forms.RequestForm;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserTransactionPairConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserTransactionPairValidator implements ConstraintValidator<UserTransactionPairConstraint, RequestForm> {

    @Override
    public void initialize(UserTransactionPairConstraint tagsConstraint) {
    }

    @Override
    public boolean isValid(RequestForm form, ConstraintValidatorContext context) {
        boolean hasUser = form.getUser() != null && !form.getUser().isEmpty();
        boolean hasType = form.getType() != null && !form.getType().isEmpty();

        return (hasUser && hasType) || (!hasUser && !hasType);
    }
}

