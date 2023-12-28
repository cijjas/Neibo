package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Entities.Tag;
import ar.edu.itba.paw.webapp.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("neighborhoods/{neighborhoodId}/tags")
@Component
public class TagController {

    @Autowired
    private TagService ts;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response listTags(
            @QueryParam("postId") final Long postId
    ) {
        List<Tag> tags = ts.getTagsByCriteria(postId, neighborhoodId);

        List<TagDto> tagsDto = tags.stream()
                .map(t -> TagDto.fromTag(t.toString(), uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<TagDto>>(tagsDto){})
                .build();
    }
}
