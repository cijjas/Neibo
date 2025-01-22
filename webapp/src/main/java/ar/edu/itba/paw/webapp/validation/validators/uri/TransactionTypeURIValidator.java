package ar.edu.itba.paw.webapp.validation.validators.uri;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.uri.TransactionTypeURIConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class TransactionTypeURIValidator implements ConstraintValidator<TransactionTypeURIConstraint, String> {

    @Override
    public void initialize(TransactionTypeURIConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String transactionTypeURI, ConstraintValidatorContext context) {
        if (transactionTypeURI == null)
            return true;
        if (!URIValidator.validateURI(transactionTypeURI, Endpoint.TRANSACTION_TYPES))
            return false;
        try {
            TransactionType.fromId(extractFirstId(transactionTypeURI));
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }
}
