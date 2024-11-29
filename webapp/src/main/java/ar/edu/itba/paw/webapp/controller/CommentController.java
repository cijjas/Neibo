package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
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

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final CommentService cs;

    @Autowired
    public CommentController(CommentService cs) {
        this.cs = cs;
    }

    @GET
    public Response listComments(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint final Long postId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Content
        final List<Comment> comments = cs.getComments(postId, page, size, neighborhoodId);
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
                cs.calculateCommentPages(postId, size),
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
    @Path("/{id}")
    public Response findComment(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint final Long postId,
            @PathParam("id") @GenericIdConstraint long commentId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);

        // Content
        Comment comment = cs.findComment(commentId, postId, neighborhoodId).orElseThrow(NotFoundException::new);
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
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint final Long postId,
            @Valid final CommentDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Creation & HashCode Generation
        final Comment comment = cs.createComment(form.getMessage(), extractSecondId(form.getUser()), postId);
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
    @Path("/{id}")
    @PreAuthorize("@pathAccessControlHelper.canDeleteComment(#commentId)")
    public Response deleteById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final Long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint final Long postId,
            @PathParam("id") @GenericIdConstraint final long commentId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);

        // Deletion Attempt
        if (cs.deleteComment(commentId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
