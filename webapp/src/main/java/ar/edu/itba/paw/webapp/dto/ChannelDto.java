package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.models.Entities.Channel;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ChannelDto {
    private String channel;
    private URI self; // localhost:8080/neighborhoods/{id}/channels/{id}
    private URI posts; // localhost:8080/posts/{id}

    public static ChannelDto fromChannel(Channel channel, UriInfo uriInfo){
        final ChannelDto dto = new ChannelDto();

        dto.channel = channel.getChannel();

//        dto.self = uriInfo.getBaseUriBuilder()
//                .path("neighborhoods")
//                .path(String.valueOf())
//                .path("channels")
//                .path(String.valueOf(channel.getChannelId()))
//                .build();
        dto.posts = uriInfo.getBaseUriBuilder()
                .path("posts")
                .queryParam("postedIn", String.valueOf(channel.getChannelId()))
                .build();

        return dto;
    }


    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public URI getPosts() {
        return posts;
    }

    public void setPosts(URI posts) {
        this.posts = posts;
    }
}
