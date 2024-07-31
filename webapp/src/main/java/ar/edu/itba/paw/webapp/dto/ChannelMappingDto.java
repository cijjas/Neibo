package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.ChannelMapping;

import javax.ws.rs.core.UriInfo;

public class ChannelMappingDto {
    private Links _links;

    public static ChannelMappingDto fromChannelMapping(ChannelMapping channelMapping, UriInfo uriInfo) {
        final ChannelMappingDto dto = new ChannelMappingDto();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("channel-mappings")
                .build());
        links.setChannel(uriInfo.getBaseUriBuilder()
                .path("channels")
                .path(String.valueOf(channelMapping.getChannel().getChannelId()))
                .build());
        links.setNeighborhood(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(channelMapping.getNeighborhood().getNeighborhoodId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
