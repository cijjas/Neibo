package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.AmenityDatePairConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.DateConstraint;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;

@AmenityDatePairConstraint
public class ShiftParams {

    @QueryParam(QueryParameter.FOR_AMENITY)
    @Pattern(regexp = URIValidator.AMENITY_URI_REGEX)
    String amenity;

    @QueryParam(QueryParameter.FOR_DATE)
    @DateConstraint
    String date;

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
