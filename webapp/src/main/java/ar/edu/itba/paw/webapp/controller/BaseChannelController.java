package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.BaseChannel;
import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.webapp.dto.BaseChannelDto;
import ar.edu.itba.paw.webapp.dto.LanguageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.generateETag;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - Post Criteria, the Base Channels are present in every neighborhood, these are ANNOUNCEMENTS, COMPLAINTS, FEED, WORKERS, RESERVATIONS, INFORMATION
 *
 * # Use cases
 *   - Base Channels are listed
 *   - Base Channels are used for filtering the posts
 *
 * # Issues
 *   - The naming is totally wrong, it mixes base channels and main menu options, more information in the Notion
 *
 * # Embeddable? I don't think so
 *   - It has to be listed and used in filtering (it has to have a URN)
*/

@Path("base-channels")
@Component
public class BaseChannelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseChannelController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBaseChannels() {
        LOGGER.info("GET request arrived at '/base-channels'");

        // Content
        BaseChannel[] baseChannels = BaseChannel.values();
        String baseChannelsHashCode = String.valueOf(Arrays.hashCode(baseChannels));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(baseChannelsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<BaseChannelDto> baseChannelDto = Arrays.stream(baseChannels)
                .map(tt -> BaseChannelDto.fromBaseChannel(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<BaseChannelDto>>(baseChannelDto){})
                .cacheControl(cacheControl)
                .tag(baseChannelsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findBaseChannel(
            @PathParam("id") final int baseChannelId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/base-channels/{}'", baseChannelId);

        // Content
        BaseChannel baseChannel = BaseChannel.fromId(baseChannelId);
        String baseChannelHashCode = String.valueOf(baseChannel.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(baseChannelHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        BaseChannelDto baseChannelDto = BaseChannelDto.fromBaseChannel(baseChannel, uriInfo);

        return Response.ok(baseChannelDto)
                .tag(baseChannelHashCode)
                .cacheControl(cacheControl)
                .build();
    }
}