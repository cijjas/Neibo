package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ChannelDto {

    @NotNull(groups = Null.class)
    @Size(min = 1, max = 20, groups = Basic.class)
    private String name;

    private Links _links;

    public static ChannelDto fromChannel(Channel channel, UriInfo uriInfo, Long neighborhoodIdLong) {
        final ChannelDto dto = new ChannelDto();

        dto.name = channel.getChannel();

        Links links = new Links();

        String neighborhoodId = String.valueOf(neighborhoodIdLong);
        String channelId = String.valueOf(channel.getChannelId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder channelUri = neighborhoodUri.clone().path(Endpoint.CHANNELS).path(channelId);
        UriBuilder postsUri = neighborhoodUri.clone().path(Endpoint.POSTS).queryParam(QueryParameter.IN_CHANNEL, channelUri.build());

        links.setSelf(channelUri.build());
        links.setPosts(postsUri.build());

        dto.set_links(links);
        return dto;
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
