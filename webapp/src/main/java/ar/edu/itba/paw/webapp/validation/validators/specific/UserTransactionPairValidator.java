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
        System.out.println(form);
        boolean hasUser = form.getRequestedBy() != null && !form.getRequestedBy().isEmpty();
        boolean hasType = form.getWithType() != null && !form.getWithType().isEmpty();

        return (hasUser && hasType) || (!hasUser && !hasType);
    }
}

