package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.PostStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - Post Criteria, a Post can be Hot, Trending or none of those two
 *   - This attribute is exclusively used for Posts
 *
 * # Use cases
 *   - A Neighbor/Admin can filter the Posts through this criteria
 */

@Path(Endpoint.API + "/" + Endpoint.POST_STATUSES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class PostStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listPostStatuses() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        PostStatus[] postStatuses = PostStatus.values();
        String postStatusesHashCode = String.valueOf(Arrays.hashCode(postStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(postStatusesHashCode));
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<PostStatusDto> postStatusDto = Arrays.stream(postStatuses)
                .map(tt -> PostStatusDto.fromPostStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<PostStatusDto>>(postStatusDto) {
                })
                .cacheControl(cacheControl)
                .tag(postStatusesHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.POST_STATUS_ID + "}")
    public Response findPostStatus(
            @PathParam(PathParameter.POST_STATUS_ID) long postStatusId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        PostStatus postStatus = PostStatus.fromId(postStatusId).orElseThrow(NotFoundException::new);
        String postStatusHashCode = String.valueOf(postStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(postStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(PostStatusDto.fromPostStatus(postStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(postStatusHashCode)
                .build();
    }
}