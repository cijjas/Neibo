package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.dto.CommentDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

/*
 * # Summary
 *   - A Post has many Comments and a User has many Comments
 *
 * # Use cases
 *   - A User/Admin Comments on a Post
 *   - A User/Admin can list the Comments for a specific Post
 *   - An Admin deletes a Comment
 */

@Path("neighborhoods/{neighborhoodId}/posts/{postId}/comments")
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
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint Long postId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

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
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts" + postId + "/comments",
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
    @Path("/{commentId}")
    public Response findComment(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint Long postId,
            @PathParam("commentId") @GenericIdConstraint long commentId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);

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
    @Validated(CreateValidationSequence.class)
    public Response createComment(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint Long postId,
            @Valid CommentDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Path Verification
        ps.findPost(neighborhoodId, postId).orElseThrow(NotFoundException::new);

        // Creation & HashCode Generation
        final Comment comment = cs.createComment(extractSecondId(createForm.getUser()), postId, createForm.getMessage());
        String commentHashCode = String.valueOf(comment.hashCode());

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getCommentId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(commentHashCode)
                .build();
    }

    @DELETE
    @Path("/{commentId}")
    @PreAuthorize("@pathAccessControlHelper.canDeleteComment(#commentId)")
    public Response deleteComment(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint Long postId,
            @PathParam("commentId") @GenericIdConstraint long commentId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);

        // Deletion Attempt
        if (cs.deleteComment(neighborhoodId, postId, commentId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
