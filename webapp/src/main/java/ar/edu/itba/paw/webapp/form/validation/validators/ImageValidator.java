package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.webapp.form.validation.constraints.ImageConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;




// https://www.baeldung.com/spring-mvc-custom-validator
public class ImageValidator implements
        ConstraintValidator<ImageConstraint, String> {


    @Override
    public void initialize(ImageConstraint imageConstraint) {

    }

    // el string es lo que queres validar
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
