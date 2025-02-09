package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class CommentDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 512)
    private String message;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    private Date creationDate;
    private Links _links;

    public static CommentDto fromComment(Comment comment, UriInfo uriInfo) {
        final CommentDto dto = new CommentDto();

        dto.message = comment.getComment();
        dto.creationDate = comment.getDate();

        Links links = new Links();

        String neighborhoodId = String.valueOf(comment.getUser().getNeighborhood().getNeighborhoodId());
        String postId = String.valueOf(comment.getPost().getPostId());
        String userId = String.valueOf((comment.getUser().getUserId()));
        String commentId = String.valueOf(comment.getCommentId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder postUri = neighborhoodUri.clone().path(Endpoint.POSTS).path(postId);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS).path(userId);
        UriBuilder commentUri = postUri.clone().path(Endpoint.COMMENTS).path(commentId);

        links.setSelf(commentUri.build());
        links.setPost(postUri.build());
        links.setCommentUser(userUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
