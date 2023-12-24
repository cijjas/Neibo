package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Worker;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TagDto {

    private String tag;
    private URI self;
    private URI posts;

    public static TagDto fromTag(final String tag, final UriInfo uriInfo){
        final TagDto dto = new TagDto();

        dto.tag = tag;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("tags")
                .path(String.valueOf(tag))
                .build();

        dto.posts = uriInfo.getBaseUriBuilder()
                .path("posts")
                .queryParam("tag", String.valueOf(tag))
                .build();

        return dto;
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

}
