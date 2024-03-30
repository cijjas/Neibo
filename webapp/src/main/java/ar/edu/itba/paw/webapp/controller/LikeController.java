package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.dto.InquiryDto;
import ar.edu.itba.paw.webapp.dto.LikeCountDto;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.form.LikeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring4.processor.SpringOptionInSelectFieldTagProcessor;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    public LikeController(final UserService us) {
        super(us);
    }

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

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<Like> likes = ls.getLikes(neighborhoodId, postId, userId, page, size);
        if (likes.isEmpty())
            return Response.noContent().build();
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
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/count")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response countLikes(
            @QueryParam("postId") final Long postId,
            @QueryParam("userId") final Long userId
    ){
        // Check Caching
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
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
            @Valid LikeForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        // Check If-Match Header
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Usual Flow
        final Like like = ls.createLike(form.getPostURN(), getLoggedUser().getUserId());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(like.getId())).build();
        entityLevelETag = ETagUtility.generateETag();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(
            @QueryParam("userId") final long userId,
            @QueryParam("postId") final long postId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/likes/'", neighborhoodId);

        // No need for If-Match Management as the resource cant be altered

        if(ls.deleteLike(postId, userId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

