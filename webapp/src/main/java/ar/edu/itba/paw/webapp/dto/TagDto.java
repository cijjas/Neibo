package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.models.Entities.Worker;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TagDto {

    private String tag;
    private URI self;
    private URI posts;

    public static TagDto fromTag(final Tag tag, final long neighborhoodId, final UriInfo uriInfo){
        final TagDto dto = new TagDto();

        dto.tag = tag.getTag();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("tags")
                .path(String.valueOf(tag.getTagId()))
                .build();

        dto.posts = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("posts")
                .queryParam("withTags", tag.getTag())
                .build();

        return dto;
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPosts() {
        return posts;
    }

    public void setPosts(URI posts) {
        this.posts = posts;
    }
}
