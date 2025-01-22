package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserCreationConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserCreationValidator implements ConstraintValidator<UserCreationConstraint, UserDto> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(UserCreationConstraint userURNInReviewFormConstraint) {
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        if (userDto == null || userDto.getUserRole() == null || userDto.getNeighborhood() == null)
            return true;
        if (!formAccessControlHelper.canCreateUser(userDto.getNeighborhood(), userDto.getUserRole())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(HttpStatus.FORBIDDEN.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
