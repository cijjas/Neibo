package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.ChannelURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.TagsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listPosts(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final long neighborhoodId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inChannel") @ChannelURNConstraint final String channel,
            @QueryParam("withTags") @TagsURNConstraint final List<String> tags,
            @QueryParam("withStatus") @PostStatusURNConstraint final String postStatus,
            @QueryParam("postedBy") @UserURNConstraint final String user
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // ID Extraction
        Long channelId = extractOptionalSecondId(channel);
        List<Long> tagIds = extractSecondIds(tags); // handles null an empty list correctly
        Long postStatusId = extractOptionalFirstId(postStatus);
        Long userId = extractOptionalSecondId(user);

        // Content
        final List<Post> posts = ps.getPosts(channelId, page, size, tagIds, neighborhoodId, postStatusId, userId);
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
                ps.calculatePostPages(channelId, size, tagIds, neighborhoodId, postStatusId, userId),
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
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findPostById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long postId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Content
        Post post = ps.findPost(postId, neighborhoodId).orElseThrow(NotFoundException::new);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Validated(CreateValidationSequence.class)
    public Response createPost(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final long neighborhoodId,
            @Valid PostDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Validation, Creation & ETag Generation
        final Post post = ps.createPost(form.getTitle(), form.getDescription(), extractSecondId(form.getUser()), extractSecondId(form.getChannel()), extractSecondIds(form.getTags()), extractOptionalFirstId(form.getImage()), neighborhoodId);
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
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@pathAccessControlHelper.canDeletePost(#postId)")
    public Response deleteById(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint final long neighborhoodId,
            @PathParam("id") @GenericIdConstraint final long postId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Attempt to delete the amenity
        if (ps.deletePost(postId, neighborhoodId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
