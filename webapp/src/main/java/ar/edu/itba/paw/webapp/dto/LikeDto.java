package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.validation.constraints.PostURNInLikeFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNInLikeFormConstraint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

public class LikeDto {

    @NotNull(groups = OnCreate.class)
    @PostURNInLikeFormConstraint(groups = OnCreate.class)
    private String post;

    @NotNull(groups = OnCreate.class)
    @UserURNInLikeFormConstraint(groups = OnCreate.class)
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
