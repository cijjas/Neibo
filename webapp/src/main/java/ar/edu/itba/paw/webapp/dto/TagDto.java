package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Tag;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TagDto {

    private String tag;
    private Links _links;

    public static TagDto fromTag(final Tag tag, final long neighborhoodId, final UriInfo uriInfo) {
        final TagDto dto = new TagDto();

        dto.tag = tag.getTag();

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
