package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.validation.constraints.ChannelURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.ImageURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.TagsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraintCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;
import java.util.List;

public class PostDto {
    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 100, groups = OnCreate.class)
    private String title;

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 2000, groups = OnCreate.class)
    private String description;

    @TagsURNConstraint(groups = OnCreate.class)
    private List<String> tags;

    @ImageURNConstraint(groups = OnCreate.class)
    private String image;

    @ChannelURNConstraint(groups = OnCreate.class)
    @NotNull(groups = OnCreate.class)
    private String channel;

    @NotNull(groups = OnCreate.class)
    @UserURNReferenceConstraintCreate(groups = OnCreate.class)
    private String user;

    private Date date;
    private Links _links;

    public static PostDto fromPost(Post post, UriInfo uriInfo) {
        final PostDto dto = new PostDto();

        dto.title = post.getTitle();
        dto.description = post.getDescription();
        dto.date = post.getDate();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .build();
        links.setSelf(self);
        links.setUser(uriInfo.getBaseUriBuilder()
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
            links.setPostPicture(uriInfo.getBaseUriBuilder()
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
        dto.set_links(links);
        return dto;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
