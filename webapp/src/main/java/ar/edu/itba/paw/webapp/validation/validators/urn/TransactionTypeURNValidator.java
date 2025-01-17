package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.urn.TransactionTypeURNConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class TransactionTypeURNValidator implements ConstraintValidator<TransactionTypeURNConstraint, String> {

    @Override
    public void initialize(TransactionTypeURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String transactionTypeURN, ConstraintValidatorContext context) {
        if (transactionTypeURN == null)
            return true;
        if (!URNValidator.validateURN(transactionTypeURN, Endpoint.TRANSACTION_TYPES))
            return false;
        try {
            TransactionType.fromId(extractFirstId(transactionTypeURN));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
