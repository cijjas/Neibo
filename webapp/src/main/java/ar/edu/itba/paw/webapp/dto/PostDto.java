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
        dto.postPicture = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(post.getPostPicture().getImageId()))
                .build();
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

}
