package ar.edu.itba.paw.webapp.validation.validators.form;

import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.TransactionTypeURNFormConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TransactionTypeURNFormValidator implements ConstraintValidator<TransactionTypeURNFormConstraint, String> {

    @Override
    public void initialize(TransactionTypeURNFormConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String transactionTypeURN, ConstraintValidatorContext context) {
        if (transactionTypeURN == null)
            return true;
        return URNValidator.validateURN(transactionTypeURN, "transaction-type");
    }
}
