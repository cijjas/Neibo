package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseChannel;

import javax.ws.rs.core.UriInfo;

public class BaseChannelDto {

    private BaseChannel baseChannel;
    private Links _links;

    public static BaseChannelDto fromBaseChannel(BaseChannel baseChannel, UriInfo uriInfo) {
        final BaseChannelDto dto = new BaseChannelDto();

        dto.baseChannel = baseChannel;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("base-channels")
                .path(String.valueOf(baseChannel.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public BaseChannel getBaseChannel() {
        return baseChannel;
    }

    public void setBaseChannel(BaseChannel baseChannel) {
        this.baseChannel = baseChannel;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
