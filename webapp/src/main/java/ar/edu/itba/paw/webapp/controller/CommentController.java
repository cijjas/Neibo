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

    private final CommentService cs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("postId")
    private Long postId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public CommentController(final UserService us, final CommentService cs) {
        super(us);
        this.cs = cs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listComments(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        // Check Caching
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        final List<Comment> comments = cs.getComments(postId, page, size, neighborhoodId);

        if (comments.isEmpty())
            return Response.noContent().build();

        final List<CommentDto> commentsDto = comments.stream()
                .map(c -> CommentDto.fromComment(c, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts" + postId + "/comment";
        int totalProductPages = cs.calculateCommentPages(postId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalProductPages);

        return Response.ok(new GenericEntity<List<CommentDto>>(commentsDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findComment(@PathParam("id") long commentId,
                                @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                @Context Request request) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}/comments/{}'", neighborhoodId, postId, commentId);
        Comment comment = cs.findComment(commentId, postId, neighborhoodId).orElseThrow(NotFoundException::new);
        // Use stored ETag value
        EntityTag entityTag = new EntityTag(comment.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        // Client has a valid version
        if (builder != null)
            return builder.cacheControl(cacheControl).build();
        // Client has an invalid version
        return Response.ok(CommentDto.fromComment(comment, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createComment(@Valid final CommentForm form,
                                  @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
                                  @Context Request request) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts/{}/comments'", neighborhoodId, postId);

        if (ifMatch != null) {
            Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .entity("Your cached version of the resource is outdated.")
                        .header(HttpHeaders.ETAG, entityLevelETag)
                        .build();
        }

        final Comment comment = cs.createComment(form.getComment(), getLoggedUser().getUserId(), postId);
        entityLevelETag = ETagUtility.generateETag();
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(comment.getCommentId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
