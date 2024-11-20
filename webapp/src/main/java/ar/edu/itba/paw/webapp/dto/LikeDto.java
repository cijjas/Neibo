package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.PostURNReferenceInLikeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInLikeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.PostURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Form;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Reference;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class LikeDto {

    @NotNull(groups = Null.class)
    @PostURNFormConstraint(groups = Form.class)
    @PostURNReferenceConstraint(groups = Reference.class)
    @PostURNReferenceInLikeConstraint(groups = Authorization.class)
    private String post;

    @NotNull(groups = Null.class)
    @UserURNFormConstraint(groups = Form.class)
    @UserURNReferenceConstraint(groups = Reference.class)
    @UserURNReferenceInLikeConstraint(groups = Authorization.class)
    private String user;

    private Date likeDate;
    private int likeCount;
    private Links _links;

    public static LikeDto fromLike(Like like, UriInfo uriInfo) {
        final LikeDto dto = new LikeDto();

        dto.likeDate = like.getLikeDate();

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

    public Date getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(Date likeDate) {
        this.likeDate = likeDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
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
