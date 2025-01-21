package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserCreationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserUpdateConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateConstraint, UserDto> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserUpdateConstraint userURNInReviewFormConstraint) {
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("USER UPDATE VALIDATOR");
//        System.out.println("User Id " + userDto.getUserId());
//        System.out.println("User Role" + userDto.getUserRole());
//        System.out.println("Neighborhood" + userDto.getNeighborhood());
//
//        if (userDto == null)
//            return true;
//        if (!formAccessControlHelper.canUpdateUser(userDto.getUserId(), userDto.getNeighborhood(), userDto.getUserRole())) {
//            constraintValidatorContext.disableDefaultConstraintViolation();
//            constraintValidatorContext.buildConstraintViolationWithTemplate("FORBIDDEN").addConstraintViolation();
//            return false;
//        }
        return true;
    }
}
