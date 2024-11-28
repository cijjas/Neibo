package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.PostURNReferenceInLikeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInLikeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class LikeDto {

    @NotNull(groups = Null.class)
    @PostURNConstraint(groups = URN.class)
    @PostURNReferenceInLikeConstraint(groups = Authorization.class)
    private String post;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNReferenceInLikeConstraint(groups = Authorization.class)
    private String user;

    private Date date;
    private int count;
    private Links _links;

    public static LikeDto fromLike(Like like, UriInfo uriInfo) {
        final LikeDto dto = new LikeDto();

        dto.date = like.getLikeDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("likes")
                .queryParam("likedBy", uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                        .path("users")
                        .path(String.valueOf(like.getUser().getUserId()))
                        .build())
                .queryParam("onPost", uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(like.getPost().getUser().getNeighborhood().getNeighborhoodId()))
                        .path("posts")
                        .path(String.valueOf(like.getPost().getPostId()))
                        .build())
                .build());
        links.setPost(
                uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                        .path("posts")
                        .path(String.valueOf(like.getPost().getPostId()))
                        .build());
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(like.getUser().getUserId()))
                .build()
        );
        dto.set_links(links);
        return dto;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
