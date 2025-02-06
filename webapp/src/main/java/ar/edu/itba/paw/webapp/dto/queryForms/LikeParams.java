package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnDelete;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class LikeParams {

    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private Long neighborhoodId;

    @QueryParam(QueryParameter.LIKED_BY)
    @NotNull(groups = OnDelete.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @QueryParam(QueryParameter.ON_POST)
    @NotNull(groups = OnDelete.class)
    @Pattern(regexp = URIValidator.POST_URI_REGEX)
    private String post;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getUser() {
        return user;
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
