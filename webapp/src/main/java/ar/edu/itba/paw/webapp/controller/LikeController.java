package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import ar.edu.itba.paw.webapp.form.LikeForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
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

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createLike(@Valid LikeForm form) {
        final Like like = ls.addLikeToPost(form.getPostId(), 5); //getLoggedUser
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(like.getId())).build();
        return Response.created(uri).build();
    }

}

