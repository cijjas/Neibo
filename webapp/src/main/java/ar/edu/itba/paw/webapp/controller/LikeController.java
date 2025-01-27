package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.dto.LikeCountDto;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.uri.PostURIConstraint;
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
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - Junction Table between Users and Posts, representing when a User likes the Post
 *
 * # Use cases
 *   - A Neighbor/Admin can like and remove the like from a Post
 */

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.LIKES)
@Component
@Validated
@Produces(MediaType.APPLICATION_JSON)
public class LikeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LikeController.class);
    private final LikeService ls;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public LikeController(LikeService ls) {
        this.ls = ls;
    }

    @GET
    public Response listLikes(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.LIKED_BY) @UserURIConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @PostURIConstraint String post,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhood/{}/likes'", neighborhoodId);

        // ID Extraction
        Long userId = extractOptionalFirstId(user);
        Long postId = extractOptionalSecondId(post);

        // Content
        final List<Like> likes = ls.getLikes(neighborhoodId, userId, postId, page, size);
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
                ls.calculateLikePages(neighborhoodId, userId, postId, size),
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
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.LIKED_BY) @UserURIConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @PostURIConstraint String post
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/likes/count'", neighborhoodId);

        // Content
        int count = ls.countLikes(neighborhoodId, extractOptionalFirstId(user), extractOptionalSecondId(post));
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        LikeCountDto dto = LikeCountDto.fromLikeCount(count, neighborhoodId, post, user, uriInfo);

        return Response.ok(new GenericEntity<LikeCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    public Response createLike(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @Valid @NotNull LikeDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        // Creation & HashCode Generation
        final Like like = ls.createLike(extractFirstId(createForm.getUser()), extractSecondId(createForm.getPost()));
        String likeHashCode = String.valueOf(like.hashCode());

        // Resource URI
        LikeDto likeDto = LikeDto.fromLike(like, uriInfo);

        return Response.created(likeDto.get_links().getSelf())
                .tag(likeHashCode)
                .build();
    }

    @DELETE
    @PreAuthorize("@pathAccessControlHelper.canDeleteLike(#user)")
    public Response deleteLike(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint Long neighborhoodId,
            @QueryParam(QueryParameter.LIKED_BY) @NotNull @UserURIConstraint String user,
            @QueryParam(QueryParameter.ON_POST) @NotNull @PostURIConstraint String post
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        if (ls.deleteLike(extractOptionalFirstId(user), extractOptionalSecondId(post)))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}

