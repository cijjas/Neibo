package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.form.PublishForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/posts")
@Component
public class PostController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService ps;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public PostController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listPosts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inChannel") final String channel,
            @QueryParam("withTags") final List<String> tags,
            @QueryParam("withStatus") @DefaultValue("none") final String postStatus,
            @QueryParam("postedBy") final Long userId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Post> posts = ps.getPosts(channel, page, size, tags, neighborhoodId, postStatus, userId);
        if (posts.isEmpty())
            return Response.noContent().build();
        final List<PostDto> postsDto = posts.stream()
                .map(p -> PostDto.fromPost(p, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts",
                ps.calculatePostPages(channel, size, tags, neighborhoodId,
                        postStatus, userId),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<PostDto>>(postsDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findPostById(
            @PathParam("id") final long postId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Content
        Post post = ps.findPost(postId, neighborhoodId).orElseThrow(() -> new NotFoundException("Post Not Found"));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(post.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(PostDto.fromPost(post, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPost(
            @FormDataParam("subject") String subject,
            @FormDataParam("message") String message,
            @FormDataParam("tags") String tags,
            @FormDataParam("channelURN") String channelURN,
            @FormDataParam("postImage") InputStream postImage
    )  {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Validation, Creation & ETag Generation
        PublishForm publishForm = new PublishForm();
        publishForm.setTags(tags);
        publishForm.setChannelURN(channelURN);
        publishForm.setMessage(message);
        publishForm.setSubject(subject);
        publishForm.setPostImage(postImage);
        final Post post = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), getLoggedUser().getUserId(), publishForm.getChannelURN(), publishForm.getTags(), publishForm.getPostImage());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(post.getPostId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
