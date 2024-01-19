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

    @Autowired
    public CommentController(final UserService us, final CommentService cs) {
        super(us);
        this.cs = cs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listComments(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at neighborhoods/{}/posts/{}/comments", neighborhoodId, postId);
        final List<Comment> comments = cs.getComments(postId, page, size, neighborhoodId);
        final List<CommentDto> commentsDto = comments.stream()
                .map(c -> CommentDto.fromComment(c, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts" + postId + "/comment";
        int totalProductPages = cs.calculateCommentPages(postId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalProductPages);

        return Response.ok(new GenericEntity<List<CommentDto>>(commentsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findComment(@PathParam("id") long commentId) {
        LOGGER.info("GET request arrived at neighborhoods/{}/posts/{}/comments/{}", neighborhoodId, postId, commentId);
        return Response.ok(CommentDto.fromComment(cs.findComment(commentId, postId, neighborhoodId)
                .orElseThrow(NotFoundException::new), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createComment(@Valid final CommentForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/posts/{}/comments", neighborhoodId, postId);
        final Comment comment = cs.createComment(form.getComment(), getLoggedUser().getUserId(), postId);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(comment.getCommentId())).build();
        return Response.created(uri).build();
    }
}
