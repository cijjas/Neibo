package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.ChannelDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

/*
 * # Summary
 *   - A Neighborhood can have many channels that are exclusive to their Neighborhood (ie Golf, Volley, Etc)
 *
 * # Use cases
 *   - A Neighbor/Admin can list the Channels for its Neighborhood
 *   - An Admin can create a Channel
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.CHANNELS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ChannelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelController.class);
    private final ChannelService cs;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ChannelController(ChannelService cs) {
        this.cs = cs;
    }

    @GET
    public Response listChannels(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @QueryParam(QueryParameter.IS_BASE) Boolean isBase,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        List<Channel> channels = cs.getChannels(neighborhoodId, isBase, page, size);
        String channelsHashCode = String.valueOf(channels.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(channelsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (channels.isEmpty())
            return Response.noContent()
                    .tag(channelsHashCode)
                    .build();

        List<ChannelDto> channelDto = channels.stream()
                .map(c -> ChannelDto.fromChannel(c, uriInfo, neighborhoodId)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.CHANNELS),
                cs.calculateChannelPages(neighborhoodId, isBase, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ChannelDto>>(channelDto) {
                })
                .cacheControl(cacheControl)
                .tag(channelsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.CHANNEL_ID + "}")
    public Response findChannel(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.CHANNEL_ID) long channelId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Channel channel = cs.findChannel(neighborhoodId, channelId).orElseThrow(NotFoundException::new);
        String channelHashCode = String.valueOf(channel.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(channelHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ChannelDto.fromChannel(channel, uriInfo, neighborhoodId))
                .tag(channelHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    public Response createChannel(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull ChannelDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        final Channel channel = cs.createChannel(neighborhoodId, createForm.getName());
        String channelHashCode = String.valueOf(channel.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(channel.getChannelId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(channelHashCode)
                .build();
    }
}

