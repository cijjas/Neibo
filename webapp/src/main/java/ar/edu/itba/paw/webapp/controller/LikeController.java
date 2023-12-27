package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.LikeService;
import ar.edu.itba.paw.models.Entities.Like;
import ar.edu.itba.paw.webapp.dto.LikeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("likes")
@Component
public class LikeController {

    @Autowired
    private LikeService ls;

    @Context
    private UriInfo uriInfo;

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

