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

@Path("base-channels")
@Component
public class BaseChannelController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseChannelController.class);

    @Context
    private UriInfo uriInfo;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listBaseChannels(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                     @Context Request request) {
        LOGGER.info("GET request arrived at '/base-channels'");

        List<BaseChannelDto> baseChannelDto = Arrays.stream(BaseChannel.values())
                .map(tt -> BaseChannelDto.fromBaseChannel(tt, uriInfo))
                .collect(Collectors.toList());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(new GenericEntity<List<BaseChannelDto>>(baseChannelDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findBaseChannel(@PathParam("id") final int id,
                                    @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                    @Context Request request) {
        LOGGER.info("GET request arrived at '/base-channels/{}'", id);

        BaseChannel baseChannel = BaseChannel.fromId(id);

        // Assuming BaseChannelDto.fromBaseChannel generates the content for the DTO
        BaseChannelDto baseChannelDto = BaseChannelDto.fromBaseChannel(baseChannel, uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(baseChannelDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

}