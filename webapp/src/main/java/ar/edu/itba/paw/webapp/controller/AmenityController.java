package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("neighborhood/{neighborhoodId}/amenities")
@Component
public class AmenityController {

    @Autowired
    private AmenityService as;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private String neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAmenities(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size) {
        final List<Amenity> amenities = as.getAmenities(Long.parseLong(neighborhoodId), page, size);
        final List<AmenityDto> amenitiesDto = amenities.stream()
                .map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        // Add pagination links to the response header
        String baseUri = uriInfo.getBaseUri().toString();
        int totalAmenityPages = as.getTotalAmenitiesPages(Long.parseLong(neighborhoodId), size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto){})
                .links(links)
                .build();
    }

    // ESTA FUNCION HAY QUE MODULARIZARLA YA QUE VA A SER UTILIZADA EN TODO LO QUE ESTE PAGINADO
    private Link[] createPaginationLinks(String baseUri, int page, int size, int totalPages) {
        List<Link> links = new ArrayList<>();

        // Self link
        links.add(Link.fromUri(baseUri + "neighborhood/" + neighborhoodId + "/amenities?page=" + page + "&size=" + size)
                .rel("self").build());

        // First page link
        links.add(Link.fromUri(baseUri + "neighborhood/" + neighborhoodId + "/amenities?page=1&size=" + size)
                .rel("first").build());

        // Last page link
        links.add(Link.fromUri(baseUri + "neighborhood/" + neighborhoodId + "/amenities?page=" + totalPages + "&size=" + size)
                .rel("last").build());

        // Previous page link
        if (page > 1) {
            links.add(Link.fromUri(baseUri + "neighborhood/" + neighborhoodId + "/amenities?page=" + (page - 1) + "&size=" + size)
                    .rel("prev").build());
        }

        // Next page link
        if (page < totalPages) {
            links.add(Link.fromUri(baseUri + "neighborhood/" + neighborhoodId + "/amenities?page=" + (page + 1) + "&size=" + size)
                    .rel("next").build());
        }

        return links.toArray(new Link[0]);
    }


    /*@POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAmenity(final AmenityDto amenityDto) {
        final Amenity amenity = as.createAmenity(amenityDto.getName(), amenityDto.getDescription(), neighborhoodId, LISTA DE SHIFTS);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getId())).build();
        return Response.created(uri).build();
    }*/
    /*
    * Hagan los DTOs en general, despues los revisamos
    * Para los POST hay que hacer un DTO __aparte__ para la creacion que seria practicamenteel formulario y tiene que ser validado,
    * para la validacion se puede usar spring validation aka copiar y pegar las anotaciones (chequear clase de sotuyo)
    *
    * Aparte de esto
    * El GET de id deberia ser facil
    * El DELETE tambn deberia ser facil
    * */



}

