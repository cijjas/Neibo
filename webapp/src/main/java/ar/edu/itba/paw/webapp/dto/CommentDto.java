package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class CommentDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 500, groups = Basic.class)
    private String comment;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    private Date date;
    private Links _links;

    public static CommentDto fromComment(Comment comment, UriInfo uriInfo) {
        final CommentDto dto = new CommentDto();

        dto.comment = comment.getComment();
        dto.date = comment.getDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(comment.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(comment.getPost().getPostId()))
                .path("comments")
                .path(String.valueOf(comment.getCommentId()))
                .build());
        links.setUser(uriInfo.getBaseUriBuilder()
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
