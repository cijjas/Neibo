package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.ProductStatus;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
public class PostStatusDto {

    private PostStatus postStatus;

    private URI self;

    public static PostStatusDto fromPostStatus(PostStatus postStatus, UriInfo uriInfo){
        final PostStatusDto dto = new PostStatusDto();

        dto.postStatus = postStatus;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("post-statuses")
                .path(String.valueOf(postStatus.getId()))
                .build();

        return dto;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
