package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("likes")
@Component
public class LikeController {

    @Autowired
    private LikeService ls;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listLikes(
            @QueryParam("postId") @DefaultValue("0") final int postId,
            @QueryParam("userId") @DefaultValue("0") final int userId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size){
        final List<Like> likes = ls.getLikesByCriteria(postId, userId, page, size);
        final List<LikeDto> likesDto = likes.stream()
                .map(l -> LikeDto.fromLike(l, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "likes";
        int totalLikePages = ls.getTotalLikePagesByCriteria(postId, userId, size);
        Link[] links = ControllerUtils.createPaginationLinks(baseUri, page, size, totalLikePages);

        return Response.ok(new GenericEntity<List<LikeDto>>(likesDto){})
                .links(links)
                .build();
    }


    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findById(@QueryParam("id") final long id) {
        Optional<Like> like = ls.findLikeById(id);
        if (!like.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(LikeDto.fromLike(like.get(), uriInfo)).build();
    }

}

