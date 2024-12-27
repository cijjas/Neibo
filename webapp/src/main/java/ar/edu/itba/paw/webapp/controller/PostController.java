package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.dto.PostsCountDto;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ChannelURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.PostStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.TagsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
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
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - Main entity of the Forum Functionality
 *   - Has many relationships, Comments, Users, Likes, Images...
 *
 * # Use cases
 *   - A User can create Posts in the Base Channels allowed for Users
 *   - An Admin can create Posts in the Channels allowed for Admins
 *   - User/Admin can view the posts in all Channels
 *   - An Admin can delete a Post
 */

@Path("neighborhoods/{neighborhoodId}/posts")
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final PostService ps;

    @Autowired
    public PostController(PostService ps) {
        this.ps = ps;
    }

    @GET
    public Response listPosts(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam("postedBy") @UserURNConstraint String user,
            @QueryParam("inChannel") @ChannelURNConstraint String channel,
            @QueryParam("withTags") @TagsURNConstraint List<String> tags,
            @QueryParam("withStatus") @PostStatusURNConstraint String postStatus,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // ID Extraction
        Long channelId = extractOptionalSecondId(channel);
        List<Long> tagIds = extractSecondIds(tags);
        Long postStatusId = extractOptionalFirstId(postStatus);
        Long userId = extractOptionalSecondId(user);

        // Content
        final List<Post> posts = ps.getPosts(neighborhoodId, userId, channelId, tagIds, postStatusId, page, size);
        String postsHashCode = String.valueOf(posts.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(postsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (posts.isEmpty())
            return Response.noContent()
                    .tag(postsHashCode)
                    .build();

        final List<PostDto> postsDto = posts.stream()
                .map(p -> PostDto.fromPost(p, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts",
                ps.calculatePostPages(neighborhoodId, userId, channelId, tagIds, postStatusId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<PostDto>>(postsDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(postsHashCode)
                .build();
    }

    @GET
    @Path("/count")
    public Response countPosts(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam("postedBy") @UserURNConstraint String user,
            @QueryParam("inChannel") @ChannelURNConstraint String channel,
            @QueryParam("withTags") @TagsURNConstraint List<String> tags,
            @QueryParam("withStatus") @PostStatusURNConstraint String postStatus
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/count'", neighborhoodId);

        // ID Extraction
        Long channelId = extractOptionalSecondId(channel);
        List<Long> tagIds = extractSecondIds(tags);
        Long postStatusId = extractOptionalFirstId(postStatus);
        Long userId = extractOptionalSecondId(user);

        // Content
        int count = ps.countPosts(neighborhoodId, userId, channelId, tagIds, postStatusId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        PostsCountDto dto = PostsCountDto.fromPostsCount(count, neighborhoodId,  uriInfo);

        return Response.ok(new GenericEntity<PostsCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path("/{postId}")
    public Response findPost(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint long postId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Content
        Post post = ps.findPost(neighborhoodId, postId).orElseThrow(NotFoundException::new);
        String postHashCode = String.valueOf(post.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(postHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(PostDto.fromPost(post, uriInfo))
                .cacheControl(cacheControl)
                .tag(postHashCode)
                .build();
    }

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createPost(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @Valid PostDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Validation, Creation & ETag Generation
        final Post post = ps.createPost(neighborhoodId, extractSecondId(createForm.getUser()), createForm.getTitle(), createForm.getBody(), extractSecondId(createForm.getChannel()), extractSecondIds(createForm.getTags()), extractOptionalFirstId(createForm.getImage()));
        String postHashCode = String.valueOf(post.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(post.getPostId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(postHashCode)
                .build();
    }

    @DELETE
    @Path("/{postId}")
    @PreAuthorize("@pathAccessControlHelper.canDeletePost(#postId)")
    public Response deletePost(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("postId") @GenericIdConstraint long postId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Attempt to delete the amenity
        if (ps.deletePost(neighborhoodId, postId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
