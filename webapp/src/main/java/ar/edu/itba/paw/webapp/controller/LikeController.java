package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.dto.LikeCountDto;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.PostURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
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

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractOptionalSecondId;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractSecondId;

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

@Path("likes")
@Component
@Validated
public class LikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService ls;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @PreAuthorize("@accessControlHelper.canListLikes(#post, #user)")
    public Response listLikes(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("onPost") @PostURNConstraint final String post,
            @QueryParam("likedBy") @UserURNConstraint final String user
    ) {
        LOGGER.info("GET request arrived at '/likes'");

        // ID Extraction
        Long postId = extractOptionalSecondId(post);
        Long userId = extractOptionalSecondId(user);

        // Content
        final List<Like> likes = ls.getLikes(postId, userId, page, size);
        String likesHashCode;

        // This is required to keep a consistent hash code across creates and this endpoint used as a find
        if (likes.size() == 1) {
            Like singleLike = likes.get(0);
            likesHashCode = String.valueOf(singleLike.hashCode());
        } else {
            likesHashCode = String.valueOf(likes.hashCode());
        }

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
                uriInfo.getBaseUri().toString() + "/likes",
                ls.calculateLikePages(postId, userId, size),
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response countLikes(
            @QueryParam("onPost") @PostURNConstraint final String post,
            @QueryParam("likedBy") @UserURNConstraint final String user
    ) {
        LOGGER.info("GET request arrived at '/likes/count'");

        // Content
        int count = ls.countLikes(extractOptionalSecondId(post), extractOptionalSecondId(user));
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        LikeCountDto dto = LikeCountDto.fromLikeCount(count, post, user, uriInfo);

        return Response.ok(new GenericEntity<LikeCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createLike(
            @Valid LikeDto form
    ) {
        LOGGER.info("POST request arrived at '/likes'");

        // Creation & HashCode Generation
        final Like like = ls.createLike(extractSecondId(form.getPost()), extractSecondId(form.getUser()));
        String likeHashCode = String.valueOf(like.hashCode());

        // Resource URN
        final URI uri = uriInfo.getBaseUriBuilder()
                .path("likes")
                .queryParam("likedBy", uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(like.getUser().getNeighborhood().getNeighborhoodId()))
                        .path("users")
                        .path(String.valueOf(like.getUser().getUserId()))
                        .build())
                .queryParam("onPost", uriInfo.getBaseUriBuilder()
                        .path("neighborhoods")
                        .path(String.valueOf(like.getPost().getUser().getNeighborhood().getNeighborhoodId()))
                        .path("posts")
                        .path(String.valueOf(like.getPost().getPostId()))
                        .build())
                .build();

        return Response.created(uri)
                .tag(likeHashCode)
                .build();
    }

    @DELETE
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteLike(#user)")
    public Response deleteById(
            @QueryParam("onPost") @NotNull @PostURNConstraint final String post,
            @QueryParam("likedBy") @NotNull @UserURNConstraint final String user
    ) {
        LOGGER.info("DELETE request arrived at '/likes'");

        if (ls.deleteLike(extractOptionalSecondId(post), extractOptionalSecondId(user)))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

