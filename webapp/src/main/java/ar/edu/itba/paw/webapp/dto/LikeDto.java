package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Booking;
import ar.edu.itba.paw.models.Entities.Like;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class LikeDto {
    private URI self;
    private URI post; // localhost:8080/posts/{id}QUERYPARAM
    private URI user; // localhost:8080/users/{id}QUERYPARAM

    public static LikeDto fromAmenity(Like like, UriInfo uriInfo){
        final LikeDto dto = new LikeDto();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("likes")
                .path(String.valueOf(like.getId()))
                .build();
        dto.post = uriInfo.getBaseUriBuilder()
                .path("posts")
                .queryParam("likedBy", String.valueOf(like.getUser().getUserId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
                .queryParam("likedPost", String.valueOf(like.getPost().getPostId()))
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
