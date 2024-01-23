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

    private final LikeService ls;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public LikeController(final UserService us, final LikeService ls) {
        super(us);
        this.ls = ls;
    }

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response listLikes(
            @QueryParam("onPost") final Long postId,
            @QueryParam("likedBy") final Long userId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/likes'", neighborhoodId);

        final List<Like> likes = ls.getLikes(neighborhoodId, postId, userId, page, size);

        if (likes.isEmpty())
            return Response.noContent().build();

        final List<LikeDto> likesDto = likes.stream()
                .map(l -> LikeDto.fromLike(l, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "likes";
        int totalLikePages = ls.calculateLikePages(neighborhoodId, postId, userId, size);
        Link[] links = ControllerUtils.createPaginationLinks(baseUri, page, size, totalLikePages);

        return Response.ok(new GenericEntity<List<LikeDto>>(likesDto) {})
                .links(links)
                .build();
    }

    @GET
    @Path("/count")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response countLikes(
            @QueryParam("postId") final Long postId,
            @QueryParam("userId") final Long userId

    ){
        int count = ls.countLikes(neighborhoodId, postId, userId);


        LikeCountDto dto = LikeCountDto.fromLikeCount(count, postId, userId, neighborhoodId,  uriInfo);
        return Response.ok(new GenericEntity<LikeCountDto>(dto) {})
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createLike(@Valid LikeForm form) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/likes'", neighborhoodId);
        final Like like = ls.createLike(form.getPostId(), getLoggedUser().getUserId());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(like.getId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@QueryParam("userId") final long userId,
                               @QueryParam("postId") final long postId) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/likes/'", neighborhoodId);
        ls.deleteLike(postId, userId);
        return Response.noContent().build();
    }
}

