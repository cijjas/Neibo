package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ChannelService;
import ar.edu.itba.paw.models.Entities.Channel;
import ar.edu.itba.paw.webapp.dto.ChannelDto;
import ar.edu.itba.paw.webapp.form.NewChannelForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Neighborhood can have many channels that are exclusive to their Neighborhood (ie Golf, Volley, Etc)
 *
 * # Use cases
 *   - A User/Admin lists the Channels for its Neighborhood
 *   - An Admin creates/deletes a Channel
 *
 * # Issues
 *   - This part has to be restructured, there is more information in the notion, but basically there is a mix between the base channels, the channels and the menu options
 */

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

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listChannels(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/channels'", neighborhoodId);

        // Content
        List<Channel> channels = cs.getChannels(neighborhoodId);
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
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/channels",
                cs.calculateChannelPages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<ChannelDto>>(channelDto){})
                .cacheControl(cacheControl)
                .tag(channelsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findChannel(
            @PathParam("id") long channelId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/channels/{}'", neighborhoodId, channelId);

        // Content
        Channel channel = cs.findChannel(channelId, neighborhoodId).orElseThrow(() -> new NotFoundException("Channel Not Found"));
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
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createChannel(
            @Valid @NotNull NewChannelForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/channels'", neighborhoodId);

        // Content
        final Channel channel = cs.createChannel(neighborhoodId, form.getName());
        String channelHashCode = String.valueOf(channel.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(channel.getChannelId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(channelHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(
            @PathParam("id") final long channelId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/channels/{}'", neighborhoodId, channelId);

        // Deletion Attempt
        if(cs.deleteChannel(channelId, neighborhoodId)) {
            return Response.noContent()
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

