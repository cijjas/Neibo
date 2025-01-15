package ar.edu.itba.paw.webapp.validation.validators.authorization;

import ar.edu.itba.paw.webapp.auth.FormAccessControlHelper;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.NeighborhoodUserRoleConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NeighborhoodUserRoleValidator implements ConstraintValidator<NeighborhoodUserRoleConstraint, UserDto> {

    @Autowired
    private FormAccessControlHelper formAccessControlHelper;

    @Override
    public void initialize(NeighborhoodUserRoleConstraint constraintAnnotation) {
    }

    // if the neighborhood specified is different from the one the user has then user role should also be specified
    // you can only move to none special neighborhoods
    // if user is moving the it should be with decreasing privileges
    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        if (userDto == null)
            return true;
        if (!formAccessControlHelper.canReferenceNeighborhoodUserRole(userDto.getUserId(), userDto.getNeighborhood(), userDto.getUserRole())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("FORBIDDEN")
                    .addConstraintViolation();
            return false;
        }
        return true;

    }
}
