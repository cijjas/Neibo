package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.dto.ChannelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("neighborhoods/{neighborhoodId}/channels")
@Component
public class ChannelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private ChannelService cs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listChannels() {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/channels'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Channel> channels = cs.getChannels(neighborhoodId);
        if (channels.isEmpty())
            return Response.noContent().build();
        List<ChannelDto> channelDto = channels.stream()
                .map(c -> ChannelDto.fromChannel(c, uriInfo, neighborhoodId)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<ChannelDto>>(channelDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findChannel(
            @PathParam("id") long channelId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/channels/{}'", neighborhoodId, channelId);
        Channel channel = cs.findChannel(channelId, neighborhoodId).orElseThrow(NotFoundException::new);
        // Use stored ETag value
        EntityTag entityTag = new EntityTag(channel.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        // Client has a valid version
        if (builder != null)
            return builder.cacheControl(cacheControl).build();
        // Client has an invalid version
        return Response.ok(ChannelDto.fromChannel(channel, uriInfo, neighborhoodId))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }
}

