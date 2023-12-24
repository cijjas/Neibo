package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Comment;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class CommentDto {

    private String comment;
    private Date date;
    private URI self;
    private URI user; // localhost:8080/users/{id}
    private URI post; // localhost:8080/posts/{id}

    public static CommentDto fromComment(Comment comment, UriInfo uriInfo){
        final CommentDto dto = new CommentDto();

        dto.comment = comment.getComment();
        dto.date = comment.getDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(comment.getPost().getPostId()))
                .path("comments")
                .path(String.valueOf(comment.getCommentId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(comment.getUser().getUserId()))
                .build();
        dto.post = uriInfo.getBaseUriBuilder()
                .path("posts")
                .path(String.valueOf(comment.getPost().getPostId()))
                .build();

        return dto;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getPost() {
        return post;
    }

    public void setPost(URI post) {
        this.post = post;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
