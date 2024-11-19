package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ChannelDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 1, max = 20, groups = OnCreate.class)
    private String name;

    private String channel;
    private Links _links;

    public static ChannelDto fromChannel(Channel channel, UriInfo uriInfo, Long neighborhoodId) {
        final ChannelDto dto = new ChannelDto();

        dto.channel = channel.getChannel();

        Links links = new Links();
        URI self = uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(neighborhoodId))
                .path("channels")
                .path(String.valueOf(channel.getChannelId()))
                .build();
        links.setSelf(self);
        links.setPosts(uriInfo.getBaseUriBuilder()
                .path("posts")
                .queryParam("inChannel", self)
                .build());
        dto.set_links(links);
        return dto;
    }


    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
