package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.CommentDto;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

/*
 * # Summary
 *   - A Post has many Comments and a User has many Comments
 *
 * # Use cases
 *   - A Neighbor/Admin Comments on a Post
 *   - A Neighbor/Admin can list the Comments for a specific Post
 *   - An Admin can delete a Comment
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.POSTS + "/{" + PathParameter.POST_ID + "}/" + Endpoint.COMMENTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    private final CommentService cs;

    private final PostService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Autowired
    public CommentController(CommentService cs, PostService ps) {
        this.cs = cs;
        this.ps = ps;
    }

    @GET
    public Response listComments(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) Long neighborhoodId,
            @PathParam(PathParameter.POST_ID) Long postId,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Verify path
        ps.findPost(neighborhoodId, postId).orElseThrow(NotFoundException::new);

        // Content
        final List<Comment> comments = cs.getComments(neighborhoodId, postId, size, page);
        String commentsHashCode = String.valueOf(comments.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(commentsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (comments.isEmpty())
            return Response.noContent()
                    .tag(commentsHashCode)
                    .build();

        final List<CommentDto> commentsDto = comments.stream()
                .map(c -> CommentDto.fromComment(c, uriInfo)).collect(Collectors.toList());

        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.POSTS).path(String.valueOf(postId)).path(Endpoint.COMMENTS),
                cs.calculateCommentPages(neighborhoodId, postId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<CommentDto>>(commentsDto) {
                })
                .cacheControl(cacheControl)
                .tag(commentsHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("{" + PathParameter.COMMENT_ID + "}")
    public Response findComment(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) Long neighborhoodId,
            @PathParam(PathParameter.POST_ID) Long postId,
            @PathParam(PathParameter.COMMENT_ID) long commentId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Comment comment = cs.findComment(neighborhoodId, postId, commentId).orElseThrow(NotFoundException::new);
        String commentHashCode = String.valueOf(comment.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(commentHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(CommentDto.fromComment(comment, uriInfo))
                .cacheControl(cacheControl)
                .tag(commentHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateComment(#createForm.user)")
    public Response createComment(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) Long neighborhoodId,
            @PathParam(PathParameter.POST_ID) Long postId,
            @Valid @NotNull CommentDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Path Verification
        ps.findPost(neighborhoodId, postId).orElseThrow(NotFoundException::new);

        // Creation & HashCode Generation
        final Comment comment = cs.createComment(extractFirstId(createForm.getUser()), postId, createForm.getMessage());
        String commentHashCode = String.valueOf(comment.hashCode());

        // Resource URI
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getCommentId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(commentHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.COMMENT_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteComment(#neighborhoodId, #postId, #commentId)")
    public Response deleteComment(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.POST_ID) long postId,
            @PathParam(PathParameter.COMMENT_ID) long commentId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (cs.deleteComment(neighborhoodId, postId, commentId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
