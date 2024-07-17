package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.dto.LikeCountDto;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.form.LikeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/*
 * # Summary
 *   - Junction Table between Users and Posts, representing when a User likes the Post
 *
 * # Use cases
 *   - A User/Admin can like and remove the like from a Post
 *
 * # Embeddable?
 *   - No, it can be tempting but the whole creation plus filtering plus potentially showing liked posts or people that liked the post ruins the idea
 */

@Path("neighborhoods/{neighborhoodId}/likes")
@Component
public class LikeController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService ls;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response listLikes(
            @QueryParam("onPost") final Long postId,
            @QueryParam("likedBy") final Long userId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        // Content
        final List<Like> likes = ls.getLikes(neighborhoodId, postId, userId, page, size);
        String likesHashCode = String.valueOf(likes.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(likesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (likes.isEmpty())
            return Response.noContent()
                    .tag(likesHashCode)
                    .build();

        final List<LikeDto> likesDto = likes.stream()
                .map(l -> LikeDto.fromLike(l, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = ControllerUtils.createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "likes",
                ls.calculateLikePages(neighborhoodId, postId, userId, size),
                page,
                size);

        return Response.ok(new GenericEntity<List<LikeDto>>(likesDto) {})
                .links(links)
                .cacheControl(cacheControl)
                .tag(likesHashCode)
                .build();
    }

    @GET
    @Path("/count")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response countLikes(
            @QueryParam("postId") final Long postId,
            @QueryParam("userId") final Long userId
    ){
        LOGGER.info("GET request arrived at '/neighborhoods/{}/likes/count'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        int count = ls.countLikes(neighborhoodId, postId, userId);
        LikeCountDto dto = LikeCountDto.fromLikeCount(count, postId, userId, neighborhoodId,  uriInfo);

        return Response.ok(new GenericEntity<LikeCountDto>(dto) {})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createLike(
            @Valid @NotNull LikeForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        // Creation & HashCode Generation
        final Like like = ls.createLike(form.getPostURN(), getRequestingUserId());
        String likeHashCode = String.valueOf(like.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(like.getId())).build();

        return Response.created(uri)
                .tag(likeHashCode)
                .build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(
            @QueryParam("userId") final long userId,
            @QueryParam("postId") final long postId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/likes/'", neighborhoodId);

        if(ls.deleteLike(postId, userId)) {
            return Response.noContent()
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

