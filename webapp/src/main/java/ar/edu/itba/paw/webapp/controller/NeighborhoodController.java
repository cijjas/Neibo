package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhood")
@Component
public class NeighborhoodController {

    @Autowired
    private NeighborhoodService ns;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listNeighborhoods(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        final List<Neighborhood> neighborhoods = ns.getNeighborhoodsByCriteria(page, size);
        final List<NeighborhoodDto> neighborhoodsDto = neighborhoods.stream()
                .map(n -> NeighborhoodDto.fromNeighborhood(n, uriInfo)).collect(Collectors.toList());

        // Add pagination links to the response header
        String baseUri = uriInfo.getBaseUri().toString();
        int totalNeighborhoodPages = ns.getTotalNeighborhoodPages(size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalNeighborhoodPages);

        return Response.ok(new GenericEntity<List<NeighborhoodDto>>(neighborhoodsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findNeighborhood(@PathParam("id") final long id) {
        Optional<Neighborhood> neighborhood = ns.findNeighborhoodById(id);
        if (!neighborhood.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(NeighborhoodDto.fromNeighborhood(neighborhood.get(), uriInfo)).build();
    }

}
