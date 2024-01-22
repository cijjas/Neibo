package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Like;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class LikeDto {
    private URI self;
    private URI post; // localhost:8080/posts/{id}
    private URI user; // localhost:8080/users/{id}

    public static LikeDto fromLike(Like like, UriInfo uriInfo){
        final LikeDto dto = new LikeDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                .path("likes")
                .queryParam("userId", like.getId().getUserId())
                .queryParam("postId", like.getId().getPostId())
                .build();
        dto.post = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(like.getPost().getPostId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(like.getUser().getUserId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPost() {
        return post;
    }

    public void setPost(URI post) {
        this.post = post;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }
}
