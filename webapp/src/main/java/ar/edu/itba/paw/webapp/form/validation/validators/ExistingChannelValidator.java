package ar.edu.itba.paw.webapp.form.validation.validators;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingChannelConstraint;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingChannelValidator implements ConstraintValidator<ExistingChannelConstraint, Long> {
    @Autowired
    ChannelService channelService;

    @Override
    public void initialize(ExistingChannelConstraint constraint) {

    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return BaseChannel.nullableFromId(id) != null;
    }

}
