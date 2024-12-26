package ar.edu.itba.paw.webapp.validation.constraints.urn;

import ar.edu.itba.paw.webapp.validation.validators.urn.TagsURNValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = TagsURNValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TagsURNConstraint {
    String message() default "Invalid URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}