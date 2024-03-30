package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.webapp.dto.CommentDto;
import ar.edu.itba.paw.webapp.form.CommentForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/posts/{postId}/comments")
@Component
public class CommentController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService cs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("postId")
    private Long postId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public CommentController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listComments(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Comment> comments = cs.getComments(postId, page, size, neighborhoodId);
        if (comments.isEmpty())
            return Response.noContent().build();
        final List<CommentDto> commentsDto = comments.stream()
                .map(c -> CommentDto.fromComment(c, uriInfo)).collect(Collectors.toList());

        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts" + postId + "/comment",
                cs.calculateCommentPages(postId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<CommentDto>>(commentsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findComment(
            @PathParam("id") long commentId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);

        // Content
        Comment comment = cs.findComment(commentId, postId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(comment.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(CommentDto.fromComment(comment, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createComment(
            @Valid final CommentForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final Comment comment = cs.createComment(form.getComment(), getLoggedUser().getUserId(), postId);
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getCommentId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
