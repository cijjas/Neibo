package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class BookingParams {

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    int page;
    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    int size;
    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private Long neighborhoodId;
    @QueryParam(QueryParameter.BOOKED_BY)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;
    @QueryParam(QueryParameter.FOR_AMENITY)
    @Pattern(regexp = URIValidator.AMENITY_URI_REGEX)
    private String amenity;

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getUser() {
        return user;
    }

    public String getAmenity() {
        return amenity;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
