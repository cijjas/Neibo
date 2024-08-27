package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingAmenityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Optional;


public class ExistingAmenityValidator implements ConstraintValidator<ExistingAmenityConstraint, Long> {
    @Autowired
    AmenityService amenityService;

    @Autowired
    UserService userService;

    @Override
    public void initialize(ExistingAmenityConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) {
            //null handled by another validator
            return true;
        }
        if (id <= 0) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid value (" + id + ") for the Amenity ID. Please use a positive integer greater than 0.")
                    .addConstraintViolation();
            return false;
        }
        Optional<Amenity> amenity = amenityService.findAmenity(id);
        if (!amenity.isPresent()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Amenity does not exist")
                    .addConstraintViolation();
            return false;
        }

        String mail = (((UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUsername();
        User user = userService.findUser(mail).orElseThrow(() -> new NotFoundException("User not found"));
        //amenity exists, now checking that it belongs to the user's neighborhood
        return (Objects.equals(amenity.get().getNeighborhood().getNeighborhoodId(), user.getNeighborhood().getNeighborhoodId()));
    }

}
