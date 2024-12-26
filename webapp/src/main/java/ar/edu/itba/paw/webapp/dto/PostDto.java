package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ChannelURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ImageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.TagsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class PostDto {
    @NotNull(groups = Null.class)
    @Size(min = 0, max = 100, groups = Basic.class)
    private String title;

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 2000, groups = Basic.class)
    private String body;

    @TagsURNConstraint(groups = URN.class)
    private List<String> tags;

    @ImageURNConstraint(groups = URN.class)
    private String image;

    @NotNull(groups = Null.class)
    @ChannelURNConstraint(groups = URN.class)
    private String channel;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    private Date creationDate;

    private Links _links;

    public static PostDto fromPost(Post post, UriInfo uriInfo) {
        final PostDto dto = new PostDto();

        dto.title = post.getTitle();
        dto.body = post.getDescription();
        dto.creationDate = post.getDate();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .build();
        links.setSelf(self);
        links.setPostUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(post.getUser().getUserId()))
                .build());
        links.setChannel(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("channels")
                .path(String.valueOf(post.getChannel().getChannelId()))
                .build());
        if (post.getPostPicture() != null) {
            links.setPostImage(uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(post.getPostPicture().getImageId()))
                    .build());
        }
        links.setComments(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .path("comments")
                .build());
        links.setTags(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("tags")
                .queryParam("post", self)
                .build());
        links.setLikes(uriInfo.getBaseUriBuilder()
                .path("likes")
                .queryParam("onPost", self)
                .build());
        links.setLikeCount(uriInfo.getBaseUriBuilder()
                .path("likes")
                .path("count")
                .queryParam("onPost", self)
                .build());
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
