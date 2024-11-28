package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.validation.constraints.specific.TagsConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TagDto {

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 20, groups = Basic.class)
    @TagsConstraint(groups = Specific.class)
    private String name;

    private Links _links;

    public static TagDto fromTag(final Tag tag, final long neighborhoodId, final UriInfo uriInfo) {
        final TagDto dto = new TagDto();

        dto.name = tag.getTag();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("tags")
                .path(String.valueOf(tag.getTagId()))
                .build();
        links.setSelf(self);
        links.setPosts(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("posts")
                .queryParam("withTag", self)
                .build());
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
