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

import static ar.edu.itba.paw.webapp.controller.ETagUtility.generateETag;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.MAX_AGE_SECONDS;

@Path("base-channels")
@Component
public class BaseChannelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseChannelController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBaseChannels() {
        LOGGER.info("GET request arrived at '/base-channels'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<BaseChannelDto> baseChannelDto = Arrays.stream(BaseChannel.values())
                .map(tt -> BaseChannelDto.fromBaseChannel(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<BaseChannelDto>>(baseChannelDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findBaseChannel(
            @PathParam("id") final int id
    ) {
        LOGGER.info("GET request arrived at '/base-channels/{}'", id);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        BaseChannelDto baseChannelDto = BaseChannelDto.fromBaseChannel(BaseChannel.fromId(id), uriInfo);

        return Response.ok(baseChannelDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}