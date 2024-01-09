package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.auth.UserAuth;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import ar.edu.itba.paw.webapp.form.NewNeighborhoodForm;
import ar.edu.itba.paw.webapp.form.PublishForm;
import ar.edu.itba.paw.webapp.security.api.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods")
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

        // felix
        System.out.println((((UserAuth)SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUsername());

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
        return Response.ok(NeighborhoodDto.fromNeighborhood(ns.findNeighborhoodById(id)
                .orElseThrow(() -> new NotFoundException("Neighborhood Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createNeighborhood(@Valid final NewNeighborhoodForm form) {
        final Neighborhood neighborhood = ns.createNeighborhood(form.getName());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(neighborhood.getNeighborhoodId())).build();
        return Response.created(uri).build();
    }

}
