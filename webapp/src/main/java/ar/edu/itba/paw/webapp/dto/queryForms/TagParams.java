package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class TagParams {

    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private long neighborhoodId;

    @QueryParam(QueryParameter.ON_POST)
    @Pattern(regexp = URIValidator.POST_URI_REGEX)
    private String post;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getPost() {
        return post;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
