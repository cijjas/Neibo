package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.PostDto;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;

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

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listPosts(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inChannel") final String channelURN,
            @QueryParam("withTags") final List<String> tagURNs,
            @QueryParam("withStatus") @DefaultValue("none") final String postStatusURN,
            @QueryParam("postedBy") final String userURN
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Post> posts = ps.getPosts(channelURN, page, size, tagURNs, neighborhoodId, postStatusURN, userURN);
        if (posts.isEmpty())
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        final List<PostDto> postsDto = posts.stream()
                .map(p -> PostDto.fromPost(p, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/posts",
                ps.calculatePostPages(channelURN, size, tagURNs, neighborhoodId,
                        postStatusURN, userURN),
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
            @PathParam("id") final long postId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(postId));
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        PostDto postDto = PostDto.fromPost(ps.findPost(postId, neighborhoodId).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(postDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPost(
            @Valid @NotNull PublishForm publishForm
    )  {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/posts'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Validation, Creation & ETag Generation
        final Post post = ps.createPost(publishForm.getSubject(), publishForm.getMessage(), getRequestingUserId(), publishForm.getChannelURN(), publishForm.getTags(), publishForm.getPostImageURN());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(post.getPostId().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(post.getPostId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canDeletePost(#postId)")
    public Response deleteById(
            @PathParam("id") final long postId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/posts/{}'", neighborhoodId, postId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(String.valueOf(postId));
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Deletion & ETag Generation Attempt
        if(ps.deletePost(postId, neighborhoodId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}
