package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Post;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class PostDto {

    private String title;
    private String description;
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
                .path("neighborhoods")
                .path(String.valueOf(post.getUser().getNeighborhood().getNeighborhoodId()))
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
}
