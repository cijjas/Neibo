package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNReferenceInLikeConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.PostURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class LikeDto {

    @NotNull(groups = Null.class)
    @PostURNConstraint(groups = URN.class)
    private String post;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNReferenceInLikeConstraint(groups = Authorization.class)
    private String user;

    private Date likeDate;

    private Links _links;

    public static LikeDto fromLike(Like like, UriInfo uriInfo) {
        final LikeDto dto = new LikeDto();

        dto.likeDate = like.getLikeDate();

        Links links = new Links();

        String neighborhoodId = String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId());
        String userId = String.valueOf(like.getUser().getUserId());
        String postId = String.valueOf(like.getPost().getPostId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder likesUri = uriInfo.getBaseUriBuilder().path(Endpoint.LIKES);
        UriBuilder userUri = neighborhoodUri.clone().path(Endpoint.USERS).path(userId);
        UriBuilder postUri = neighborhoodUri.clone().path(Endpoint.POSTS).path(postId);
        UriBuilder likeUri = likesUri.clone().queryParam(QueryParameter.LIKED_BY, userUri).queryParam(QueryParameter.ON_POST, postUri);

        links.setSelf(likeUri.build());
        links.setPost(postUri.build());
        links.setLikeUser(userUri.build());

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
