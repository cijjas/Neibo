package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.specific.AmenityDatePairConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.DateConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.AmenityURNConstraint;

import javax.ws.rs.QueryParam;

@AmenityDatePairConstraint
public class ShiftForm {

    @QueryParam(QueryParameter.FOR_AMENITY)
    @AmenityURNConstraint
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
