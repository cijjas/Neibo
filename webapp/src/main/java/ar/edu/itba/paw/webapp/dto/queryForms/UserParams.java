package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class UserParams {

    @QueryParam(QueryParameter.IN_NEIGHBORHOOD)
    @Pattern(regexp = URIValidator.NEIGHBORHOOD_URI_REGEX)
    private String neighborhood;

    @QueryParam(QueryParameter.WITH_ROLE)
    @Pattern(regexp = URIValidator.USER_ROLE_URI_REGEX)
    private String userRole;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getUserRole() {
        return userRole;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
