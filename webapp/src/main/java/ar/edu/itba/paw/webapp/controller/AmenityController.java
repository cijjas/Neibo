package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhood/{neighborhoodId}/amenities")
@Component
public class AmenityController {

    @Autowired
    private AmenityService as;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAmenities(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        final List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);
        final List<AmenityDto> amenitiesDto = amenities.stream()
                .map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/amenities";
        int totalAmenityPages = as.getTotalAmenitiesPages(neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAmenity(@PathParam("id") final long id) {
        Optional<Amenity> amenity = as.findAmenityById(id);
        if (!amenity.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(AmenityDto.fromAmenity(amenity.get(), uriInfo)).build();
    }





    /*@POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAmenity(final AmenityDto amenityDto) {
        final Amenity amenity = as.createAmenity(amenityDto.getName(), amenityDto.getDescription(), neighborhoodId, LISTA DE SHIFTS);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();
        return Response.created(uri).build();
    }*/
}

