package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.reference.TransactionTypeURNReferenceConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class TransactionTypeURNReferenceValidator implements ConstraintValidator<TransactionTypeURNReferenceConstraint, String> {

    @Override
    public void initialize(TransactionTypeURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String transactionTypeURN, ConstraintValidatorContext context) {
        if (transactionTypeURN == null)
            return true;
        try {
            TransactionType.fromId(extractId(transactionTypeURN));
        } catch (NotFoundException e){
            return false;
        }
        return true;
    }
}
