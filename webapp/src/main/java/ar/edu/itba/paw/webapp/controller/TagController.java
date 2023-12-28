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
            @QueryParam("postId") @DefaultValue("0") final Long postId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        List<Tag> tags = ts.getTagsByCriteria(postId, neighborhoodId, page, size);
        List<TagDto> tagsDto = tags.stream()
                .map(t -> TagDto.fromTag(t.toString(), uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/tags";
        int totalTagPages = ts.getTotalTagPagesByCriteria(postId, neighborhoodId, size);
        Link[] links = ControllerUtils.createPaginationLinks(baseUri, page, size, totalTagPages);

        return Response.ok(new GenericEntity<List<TagDto>>(tagsDto){})
                .links(links)
                .build();
    }

}
