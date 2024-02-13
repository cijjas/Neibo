package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.LanguageIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = LanguageIdValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface LanguageIdConstraint {
    String message() default "Language does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
