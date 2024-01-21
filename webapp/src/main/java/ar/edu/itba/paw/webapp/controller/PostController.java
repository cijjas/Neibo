package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.form.PublishForm;
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

@Path("neighborhoods/{neighborhoodId}/posts")
@Component
public class PostController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    private final PostService ps;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @Autowired
    public PostController(final UserService us, final PostService ps) {
        super(us);
        this.ps = ps;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listPosts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("channel") final String channel,
            @QueryParam("tags") final List<String> tags,
            @QueryParam("postStatus") @DefaultValue("none") final String postStatus,
            @QueryParam("userId") final Long userId) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts'", neighborhoodId);
        final List<Post> posts = ps.getPosts(channel, page, size, tags, neighborhoodId, postStatus, userId);
        final List<PostDto> postsDto = posts.stream()
                .map(p -> PostDto.fromPost(p, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts";
        int totalPostsPages = ps.calculatePostPages(channel, size, tags, neighborhoodId, postStatus, userId);
        Link[] links = createPaginationLinks(baseUri, page, size, totalPostsPages);

        return Response.ok(new GenericEntity<List<PostDto>>(postsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findPostById(@PathParam("id") final long postId) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);
        return Response.ok(PostDto.fromPost(ps.findPost(postId, neighborhoodId)
                .orElseThrow(() -> new NotFoundException("Post Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createPost(@Valid final PublishForm form) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts'", neighborhoodId);
        final Post post = ps.createPost(form.getSubject(), form.getMessage(), getLoggedUser().getUserId(), form.getChannel(), form.getTags(), form.getImageFile());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(post.getPostId())).build();
        return Response.created(uri).build();
    }

}
