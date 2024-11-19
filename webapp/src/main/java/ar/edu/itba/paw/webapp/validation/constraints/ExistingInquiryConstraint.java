package ar.edu.itba.paw.webapp.validation.constraints;

import ar.edu.itba.paw.webapp.validation.validators.ExistingInquiryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = ExistingInquiryValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingInquiryConstraint {
    String message() default "Inquiry does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}