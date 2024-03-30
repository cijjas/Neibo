package ar.edu.itba.paw.webapp.form.validation.constraints;

import javax.validation.Payload;

public @interface NeighborhoodsURNConstraint {
    String message() default "Error in the URN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
