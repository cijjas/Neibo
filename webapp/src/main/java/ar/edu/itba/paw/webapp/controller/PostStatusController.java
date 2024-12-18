package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.webapp.dto.PostStatusDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.MAX_AGE_SECONDS;


/*
 * # Summary
 *   - Post Criteria, a Post can be Hot, Trending or none of those two
 *   - This attribute is exclusively used for Posts
 *
 * # Use cases
 *   - A User/Admin can filter the Posts over this criteria
 */

@Path("post-statuses")
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
        LOGGER.info("GET request arrived at '/post-statuses'");

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
    @Path("/{postStatusId}")
    public Response findPostStatus(
            @PathParam("postStatusId") @GenericIdConstraint Long postStatusId
    ) {
        LOGGER.info("GET request arrived at '/post-statuses/{}'", postStatusId);

        // Content
        PostStatus postStatus = PostStatus.fromId(postStatusId);
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