package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.services.CommentService;
import ar.edu.itba.paw.models.Entities.Comment;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.webapp.dto.CommentDto;
import ar.edu.itba.paw.webapp.dto.ProductDto;
import ar.edu.itba.paw.webapp.form.CommentForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class CommentController {

    @Autowired
    private CommentService cs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("postId")
    private Long postId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listComments(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        final List<Comment> comments = cs.getCommentsByPostId(postId, page, size);
        final List<CommentDto> commentsDto = comments.stream()
                .map(c -> CommentDto.fromComment(c, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts" + postId + "/comment";
        int totalProductPages = cs.getTotalCommentPages(postId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalProductPages);

        return Response.ok(new GenericEntity<List<CommentDto>>(commentsDto){})
                .links(links)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createComment(@Valid final CommentForm form) {
//        final Comment comment = cs.createComment(form.getComment(), getLoggedUser, postId);
        final Comment comment = cs.createComment(form.getComment(), 1, postId);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(comment.getCommentId())).build();
        return Response.created(uri).build();
    }
}
