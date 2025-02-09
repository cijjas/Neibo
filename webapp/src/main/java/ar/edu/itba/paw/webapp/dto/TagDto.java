package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.TagsConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class TagDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 64)
    @TagsConstraint(groups = Specific.class)
    private String name;

    private Links _links;

    public static TagDto fromTag(final Tag tag, final long neighborhoodIdLong, final UriInfo uriInfo) {
        final TagDto dto = new TagDto();

        dto.name = tag.getTag();

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);
        String tagId = String.valueOf(tag.getTagId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder tagUri = neighborhoodUri.clone().path(Endpoint.TAGS).path(tagId);
        UriBuilder postsUri = neighborhoodUri.clone().path(Endpoint.POSTS).queryParam(QueryParameter.WITH_TAG, tagUri.build());

        links.setSelf(tagUri.build());
        links.setPosts(postsUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
