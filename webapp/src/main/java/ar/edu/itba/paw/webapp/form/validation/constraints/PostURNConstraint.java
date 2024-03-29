package ar.edu.itba.paw.webapp.form.validation.constraints;

import ar.edu.itba.paw.webapp.form.validation.validators.PostURNValidator;

import javax.validation.Constraint;

import javax.validation.Payload;

import java.lang.annotation.*;

@Documented

@Constraint(validatedBy = PostURNValidator.class)

@Retention(RetentionPolicy.RUNTIME)

public @interface PostURNConstraint {

    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}