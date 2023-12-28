package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Post;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class PostDto {

    private String title;
    private String description;
    private Date date;
    private URI self;
    private URI user;
    private URI channel;
    private URI postPicture;
    private URI comments;
    private URI tags;
    private URI likes;
    private URI subscribers;

    public static PostDto fromPost(Post post, UriInfo uriInfo){
        final PostDto dto = new PostDto();

        dto.title = post.getTitle();
        dto.description = post.getDescription();
        dto.date = post.getDate();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .build();
        dto.user = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(post.getUser().getUserId()))
                .build();
        dto.channel = uriInfo.getBaseUriBuilder()
                .path("channels")
                .path(String.valueOf(post.getChannel().getChannelId()))
                .build();
        if ( post.getPostPicture() != null ){
            dto.postPicture = uriInfo.getBaseUriBuilder()
                    .path("images")
                    .path(String.valueOf(post.getPostPicture().getImageId()))
                    .build();
        }
        dto.comments = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .path("comments")
                .build();
        dto.tags = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("posts")
                .path(String.valueOf(post.getPostId()))
                .path("tags")
                .build();
        dto.likes = uriInfo.getBaseUriBuilder()
                .path("likes")
                .queryParam("post", String.valueOf(post.getPostId()))
                .build();
        dto.subscribers = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .queryParam("subscribedTo", String.valueOf(post.getPostId()))
                .build();

        return dto;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public URI getChannel() {
        return channel;
    }

    public void setChannel(URI channel) {
        this.channel = channel;
    }

    public URI getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(URI postPicture) {
        this.postPicture = postPicture;
    }

    public URI getComments() {
        return comments;
    }

    public void setComments(URI comments) {
        this.comments = comments;
    }

    public URI getTags() {
        return tags;
    }

    public void setTags(URI tags) {
        this.tags = tags;
    }

    public URI getLikes() {
        return likes;
    }

    public void setLikes(URI likes) {
        this.likes = likes;
    }

    public URI getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(URI subscribers) {
        this.subscribers = subscribers;
    }
}
