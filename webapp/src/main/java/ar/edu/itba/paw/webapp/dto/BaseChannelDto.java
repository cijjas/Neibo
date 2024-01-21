package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.BaseChannel;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class BaseChannelDto {

    private BaseChannel baseChannel;

    private URI self;

    public static BaseChannelDto fromBaseChannel(BaseChannel baseChannel, UriInfo uriInfo){
        final BaseChannelDto dto = new BaseChannelDto();

        dto.baseChannel = baseChannel;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("base-channels")
                .path(String.valueOf(baseChannel.getId()))
                .build();

        return dto;
    }

    public BaseChannel getBaseChannel() {
        return baseChannel;
    }

    public void setBaseChannel(BaseChannel baseChannel) {
        this.baseChannel = baseChannel;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
