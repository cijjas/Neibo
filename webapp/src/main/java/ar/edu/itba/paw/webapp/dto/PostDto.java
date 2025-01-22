package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURIReferenceInCreationConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ChannelURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ImageURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.TagsURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.UserURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URI;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;

public class PostDto {
    @NotNull(groups = Null.class)
    @Size(min = 0, max = 100, groups = Basic.class)
    private String title;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 2000, groups = Basic.class)
    private String body;

    @TagsURIConstraint(groups = URI.class)
    private List<String> tags;

    @ImageURIConstraint(groups = URI.class)
    private String image;

    @NotNull(groups = Null.class)
    @ChannelURIConstraint(groups = URI.class)
    private String channel;

    @NotNull(groups = Null.class)
    @UserURIConstraint(groups = URI.class)
    @UserURIReferenceInCreationConstraint(groups = Authorization.class)
    private String user;

    private Date creationDate;

    private Links _links;

    public static PostDto fromPost(Post post, UriInfo uriInfo) {
        final PostDto dto = new PostDto();

        dto.title = post.getTitle();
        dto.body = post.getDescription();
        dto.creationDate = post.getDate();

        Links links = new Links();

        String neighborhoodId = String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId());
        String postId = String.valueOf(post.getPostId());
        String userId = String.valueOf(post.getUser().getUserId());
        String channelId = String.valueOf((post.getChannel().getChannelId()));

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder postUri = neighborhoodUri.clone().path(Endpoint.POSTS).path(postId);
        UriBuilder commentsUri = postUri.clone().path(Endpoint.COMMENTS);
        UriBuilder userUri = uriInfo.getBaseUriBuilder().path(Endpoint.USERS).path(userId);
        UriBuilder channelUri = neighborhoodUri.clone().path(Endpoint.CHANNELS).path(channelId);
        UriBuilder tagsUri = neighborhoodUri.clone().path(Endpoint.TAGS).queryParam(QueryParameter.ON_POST, postUri.build());
        UriBuilder likesUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.LIKES).queryParam(QueryParameter.ON_POST, postUri.build());
        UriBuilder likesCountUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId).path(Endpoint.LIKES).path(Endpoint.COUNT).queryParam(QueryParameter.ON_POST, postUri.build());

        links.setSelf(postUri.build());
        links.setPostUser(userUri.build());
        links.setChannel(channelUri.build());
        links.setComments(commentsUri.build());
        links.setTags(tagsUri.build());
        links.setLikes(likesUri.build());
        links.setLikeCount(likesCountUri.build());
        if (post.getPostPicture() != null) {
            String imageId = String.valueOf(post.getPostPicture().getImageId());
            UriBuilder imageUri = uriInfo.getBaseUriBuilder().path(Endpoint.IMAGES).path(imageId);
            links.setPostImage(imageUri.build());
        }

        dto.set_links(links);
        return dto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
