package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.TagsURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = TagsURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TagsURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}