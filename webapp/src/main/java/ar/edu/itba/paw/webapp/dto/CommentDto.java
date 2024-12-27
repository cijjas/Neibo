package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class CommentDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 500, groups = Basic.class)
    private String message;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    private Date creationDate;
    private Links _links;

    public static CommentDto fromComment(Comment comment, UriInfo uriInfo) {
        final CommentDto dto = new CommentDto();

        dto.message = comment.getComment();
        dto.creationDate = comment.getDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(comment.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(comment.getPost().getPostId()))
                .path("comments")
                .path(String.valueOf(comment.getCommentId()))
                .build());
        links.setCommentUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(comment.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(comment.getUser().getUserId()))
                .build());
        links.setPost(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(comment.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(comment.getPost().getPostId()))
                .build());
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
