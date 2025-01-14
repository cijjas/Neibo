package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.LikeCountDto;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.validation.constraints.urn.PostURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;
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

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalSecondId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractSecondId;

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

@Path(Endpoint.LIKES)
@Component
@Validated
@Produces(MediaType.APPLICATION_JSON)
public class LikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final LikeService ls;

    @Autowired
    public LikeController(LikeService ls) {
        this.ls = ls;
    }

    @GET
    @PreAuthorize("@pathAccessControlHelper.canListLikes(#post, #user)")
    public Response listLikes(
            @QueryParam(QueryParameter.LIKED_BY) @UserURNConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @PostURNConstraint String post,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/likes'");

        // ID Extraction
        Long userId = extractOptionalSecondId(user);
        Long postId = extractOptionalSecondId(post);

        // Content
        final List<Like> likes = ls.getLikes(userId, postId, page, size);
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
                uriInfo.getBaseUriBuilder().path(Endpoint.LIKES),
                ls.calculateLikePages(userId, postId, size),
                page,
                size);

        return Response.ok(new GenericEntity<List<LikeDto>>(likesDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(likesHashCode)
                .build();
    }

    @GET
    @Path(Endpoint.COUNT)
    public Response countLikes(
            @QueryParam(QueryParameter.LIKED_BY) @UserURNConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @PostURNConstraint String post
    ) {
        LOGGER.info("GET request arrived at '/likes/count'");

        // Content
        int count = ls.countLikes(extractOptionalSecondId(user), extractOptionalSecondId(post));
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
    @Validated(CreateValidationSequence.class)
    public Response createLike(
            @Valid @NotNull LikeDto createForm
    ) {
        LOGGER.info("POST request arrived at '/likes'");

        // Creation & HashCode Generation
        final Like like = ls.createLike(extractSecondId(createForm.getUser()), extractSecondId(createForm.getPost()));
        String likeHashCode = String.valueOf(like.hashCode());

        // Resource URN
        LikeDto likeDto = LikeDto.fromLike(like, uriInfo);

        return Response.created(likeDto.get_links().getSelf())
                .tag(likeHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@pathAccessControlHelper.canDeleteLike(#user)")
    public Response deleteLike(
            @QueryParam(QueryParameter.LIKED_BY) @NotNull @UserURNConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @NotNull @PostURNConstraint String post
    ) {
        LOGGER.info("DELETE request arrived at '/likes'");

        if (ls.deleteLike(extractOptionalSecondId(user), extractOptionalSecondId(post)))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

