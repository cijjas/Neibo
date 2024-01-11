package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Resource;
import ar.edu.itba.paw.models.Entities.Shift;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.form.AmenityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/amenities")
@CrossOrigin(origins = "http://localhost:4200/")
@Component
public class AmenityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);

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
        LOGGER.info("Listing Amenities");
        final List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);
        final List<AmenityDto> amenitiesDto = amenities.stream()
                .map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/amenities";
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
        LOGGER.info("Finding Amenity with id {}", id);
        return Response.ok(AmenityDto.fromAmenity(as.findAmenityById(id)
                .orElseThrow(() -> new NotFoundException("Amenity Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createAmenity(@Valid final AmenityForm form) {
        LOGGER.info("Creating Amenity");
        final Amenity amenity = as.createAmenity(form.getName(), form.getDescription(), neighborhoodId, form.getSelectedShifts());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(amenity.getAmenityId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateAmenityPartially(
            @PathParam("id") final long id,
            @Valid final AmenityForm partialUpdate) {
        LOGGER.info("Updating Amenity with id {}", id);
        final Amenity amenity = as.updateAmenityPartially(id, partialUpdate.getName(), partialUpdate.getDescription());
        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("Deleting Amenity with id {}", id);
        as.deleteAmenity(id);
        return Response.noContent().build();
    }
}

