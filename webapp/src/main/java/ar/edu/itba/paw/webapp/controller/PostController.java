package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.dto.PostsCountDto;
import ar.edu.itba.paw.webapp.dto.queryForms.PostParams;
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
    @PreAuthorize("@accessControlHelper.canListOrCountPosts(#postParams.user, #postParams.channel, #postParams.tags, #postParams.postStatus)")
    public Response listPosts(
            @Valid @BeanParam PostParams postParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractOptionalFirstId(postParams.getUser());
        Long channelId = extractOptionalSecondId(postParams.getChannel());
        List<Long> tagIds = extractSecondIds(postParams.getTags());
        Long postStatusId = extractOptionalFirstId(postParams.getPostStatus());

        // Content
        final List<Post> posts = ps.getPosts(postParams.getNeighborhoodId(), userId, channelId, tagIds, postStatusId, postParams.getPage(), postParams.getSize());
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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(postParams.getNeighborhoodId())).path(Endpoint.POSTS),
                ps.calculatePostPages(postParams.getNeighborhoodId(), userId, channelId, tagIds, postStatusId, postParams.getSize()),
                postParams.getPage(),
                postParams.getSize()
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
    @PreAuthorize("@accessControlHelper.canListOrCountPosts(#postParams.user, #postParams.channel, #postParams.tags, #postParams.postStatus)")
    public Response countPosts(
            @Valid @BeanParam PostParams postParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractOptionalFirstId(postParams.getUser());
        Long channelId = extractOptionalSecondId(postParams.getChannel());
        List<Long> tagIds = extractSecondIds(postParams.getTags());
        Long postStatusId = extractOptionalFirstId(postParams.getPostStatus());

        // Content
        int count = ps.countPosts(postParams.getNeighborhoodId(), userId, channelId, tagIds, postStatusId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        PostsCountDto dto = PostsCountDto.fromPostsCount(count, postParams.getNeighborhoodId(), postParams.getUser(), postParams.getChannel(), postParams.getTags(), postParams.getPostStatus(), uriInfo);

        return Response.ok(new GenericEntity<PostsCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.POST_ID + "}")
    public Response findPost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.POST_ID) long postId
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
    @PreAuthorize("@accessControlHelper.canCreatePost(#createForm.user, #createForm.channel, #createForm.tags)")
    public Response createPost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
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
    @PreAuthorize("@accessControlHelper.canDeletePost(#postId)")
    public Response deletePost(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.POST_ID) long postId
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
