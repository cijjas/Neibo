package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.dto.PostsCountDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.ChannelURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.PostStatusURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.TagsURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.UserURIConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 *   - A Neighbor can create Posts in the Channels allowed for Users
 *   - An Admin can create Posts in the Channels allowed for Admins
 *   - A Worker can create Posts in the Channel allowed for Workers
 *   - Neighbor/Admin can view the posts in all Channels (including Workers Channel)
 *   - A Worker can view the posts in the Workers Channel
 *   - An Admin can delete a Post
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.POSTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class PostController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostController.class);
    private final PostService ps;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public PostController(PostService ps) {
        this.ps = ps;
    }

    @GET
    public Response listPosts(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam(QueryParameter.POSTED_BY) @UserURIConstraint String user,
            @QueryParam(QueryParameter.IN_CHANNEL) @ChannelURIConstraint String channel,
            @QueryParam(QueryParameter.WITH_TAG) @TagsURIConstraint List<String> tags,
            @QueryParam(QueryParameter.WITH_STATUS) @PostStatusURIConstraint String postStatus,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractOptionalFirstId(user);
        Long channelId = extractOptionalSecondId(channel);
        List<Long> tagIds = extractSecondIds(tags);
        Long postStatusId = extractOptionalFirstId(postStatus);

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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(neighborhoodId)).path(Endpoint.POSTS),
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
    @Path(Endpoint.COUNT)
    public Response countPosts(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.POSTED_BY) @UserURIConstraint String user,
            @QueryParam(QueryParameter.IN_CHANNEL) @ChannelURIConstraint String channel,
            @QueryParam(QueryParameter.WITH_TAG) @TagsURIConstraint List<String> tags,
            @QueryParam(QueryParameter.WITH_STATUS) @PostStatusURIConstraint String postStatus
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractOptionalFirstId(user);
        Long channelId = extractOptionalSecondId(channel);
        List<Long> tagIds = extractSecondIds(tags);
        Long postStatusId = extractOptionalFirstId(postStatus);

        // Content
        int count = ps.countPosts(neighborhoodId, userId, channelId, tagIds, postStatusId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        PostsCountDto dto = PostsCountDto.fromPostsCount(count, neighborhoodId, user, channel, tags, postStatus, uriInfo);

        return Response.ok(new GenericEntity<PostsCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.POST_ID + "}")
    public Response findPost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.POST_ID) @GenericIdConstraint long postId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

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
    @Validated(CreateSequence.class)
    public Response createPost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @Valid @NotNull PostDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Validation, Creation & ETag Generation
        final Post post = ps.createPost(neighborhoodId, extractFirstId(createForm.getUser()), createForm.getTitle(), createForm.getBody(), extractSecondId(createForm.getChannel()), extractSecondIds(createForm.getTags()), extractOptionalFirstId(createForm.getImage()));
        String postHashCode = String.valueOf(post.hashCode());

        // Resource URI
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
    @Path("{" + PathParameter.POST_ID + "}")
    @PreAuthorize("@pathAccessControlHelper.canDeletePost(#postId)")
    public Response deletePost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.POST_ID) @GenericIdConstraint long postId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Attempt to delete the amenity
        if (ps.deletePost(neighborhoodId, postId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
