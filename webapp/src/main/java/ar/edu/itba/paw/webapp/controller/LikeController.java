package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.form.LikeForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
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
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listLikes(
            @QueryParam("postId") @DefaultValue("0") final int postId,
            @QueryParam("userId") @DefaultValue("0") final int userId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size){
        LOGGER.info("GET request arrived at neighborhoods/{}/likes", neighborhoodId);
        final List<Like> likes = ls.getLikesByCriteria(neighborhoodId, postId, userId, page, size);
        final List<LikeDto> likesDto = likes.stream()
                .map(l -> LikeDto.fromLike(l, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "likes";
        int totalLikePages = ls.getTotalLikePagesByCriteria(neighborhoodId, postId, userId, size);
        Link[] links = ControllerUtils.createPaginationLinks(baseUri, page, size, totalLikePages);

        return Response.ok(new GenericEntity<List<LikeDto>>(likesDto){})
                .links(links)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createLike(@Valid LikeForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/likes", neighborhoodId);
        final Like like = ls.addLikeToPost(form.getPostId(), getLoggedUser().getUserId());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(like.getId())).build();
        return Response.created(uri).build();
    }

}

