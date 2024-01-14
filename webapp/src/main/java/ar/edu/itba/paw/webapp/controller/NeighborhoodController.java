package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import ar.edu.itba.paw.webapp.form.NewNeighborhoodForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods")
@Component
public class NeighborhoodController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodController.class);

    @Autowired
    private NeighborhoodService ns;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listNeighborhoods(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        LOGGER.info("GET request arrived at neighborhoods/");
        final List<Neighborhood> neighborhoods = ns.getNeighborhoods(page, size);
        final List<NeighborhoodDto> neighborhoodsDto = neighborhoods.stream()
                .map(n -> NeighborhoodDto.fromNeighborhood(n, uriInfo)).collect(Collectors.toList());

        // Add pagination links to the response header
        String baseUri = uriInfo.getBaseUri().toString();
        int totalNeighborhoodPages = ns.calculateNeighborhoodPages(size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalNeighborhoodPages);

        return Response.ok(new GenericEntity<List<NeighborhoodDto>>(neighborhoodsDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findNeighborhood(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at neighborhoods/{}", id);
        return Response.ok(NeighborhoodDto.fromNeighborhood(ns.findNeighborhood(id)
                .orElseThrow(() -> new NotFoundException("Neighborhood Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createNeighborhood(@Valid final NewNeighborhoodForm form) {
        LOGGER.info("POST request arrived at neighborhoods/");
        final Neighborhood neighborhood = ns.createNeighborhood(form.getName());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(neighborhood.getNeighborhoodId())).build();
        return Response.created(uri).build();
    }

}
