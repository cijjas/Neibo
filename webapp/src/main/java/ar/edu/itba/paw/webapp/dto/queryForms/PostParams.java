package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.TagsURIConstraint;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

public class PostParams {

    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private long neighborhoodId;

    @QueryParam(QueryParameter.POSTED_BY)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @QueryParam(QueryParameter.IN_CHANNEL)
    @Pattern(regexp = URIValidator.CHANNEL_URI_REGEX)
    private String channel;

    @QueryParam(QueryParameter.WITH_TAG)
    @TagsURIConstraint
    private List<String> tags;

    @QueryParam(QueryParameter.WITH_STATUS)
    @Pattern(regexp = URIValidator.POST_STATUS_URI_REGEX)
    private String postStatus;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
