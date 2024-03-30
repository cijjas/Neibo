package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.PostStatusDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("post-statuses")
@Component
public class PostStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostStatusController.class);

    @Context
    private UriInfo uriInfo;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPostStatuses(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/post-statuses'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        cacheControl.setMaxAge(3600);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<PostStatusDto> postStatusDto = Arrays.stream(PostStatus.values())
                .map(tt -> PostStatusDto.fromPostStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<PostStatusDto>>(postStatusDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findPostStatus(
            @PathParam("id") final int id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/post-statuses/{}'", id);
        PostStatusDto postStatusDto = PostStatusDto.fromPostStatus(PostStatus.fromId(id), uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(postStatusDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}