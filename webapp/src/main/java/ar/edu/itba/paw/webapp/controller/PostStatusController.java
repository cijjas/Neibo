package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.PostStatus;
import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.webapp.dto.PostStatusDto;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("post-statuses")
@Component
public class PostStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostStatusController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listPostStatuses() {
        LOGGER.info("GET request arrived at '/post-statuses'");
        List<PostStatusDto> postStatusDto = Arrays.stream(PostStatus.values())
                .map(tt -> PostStatusDto.fromPostStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<PostStatusDto>>(postStatusDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findPostStatus(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/post-statuses/{}'", id);
        return Response.ok(PostStatusDto.fromPostStatus(PostStatus.fromId(id), uriInfo)).build();
    }
}