package ar.edu.itba.paw.webapp.validation.validators;

import ar.edu.itba.paw.interfaces.services.InquiryService;
import ar.edu.itba.paw.webapp.validation.constraints.ExistingInquiryConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingInquiryValidator implements ConstraintValidator<ExistingInquiryConstraint, Long> {
    @Autowired
    InquiryService inquiryService;

    @Override
    public void initialize(ExistingInquiryConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return inquiryService.findInquiry(id).isPresent();
    }

}
