package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.interfaces.services.AvailabilityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Availability;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.AvailabilityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/amenities/{amenityId}/availability")
@Component
public class AvailabilityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);

    @Autowired
    private AvailabilityService as;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @PathParam("amenityId")
    private Long amenityId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAvailability(
            @QueryParam("status") String status,
            @QueryParam("date") String date
    ) {
        LOGGER.info("Listing Availability for Amenity with id {}", amenityId);
        final List<Availability> availabilities = as.getAvailability(amenityId, status, date, neighborhoodId);
        final List<AvailabilityDto> availabilityDto = availabilities.stream()
                .map(a -> AvailabilityDto.fromAvailability(a, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<AvailabilityDto>>(availabilityDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAvailability(@PathParam("id") final long availabilityId) {
        LOGGER.info("Finding Availability with id {}", availabilityId);
        return Response.ok(AvailabilityDto.fromAvailability(as.findAvailability(amenityId, availabilityId, neighborhoodId)
                .orElseThrow(NotFoundException::new), uriInfo)).build();
    }
}
