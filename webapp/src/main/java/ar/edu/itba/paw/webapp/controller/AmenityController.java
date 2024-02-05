package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AmenityService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.form.AmenityForm;
import ar.edu.itba.paw.webapp.form.AmenityUpdateForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.getCurrentTimestampForPreconditions;

@Path("neighborhoods/{neighborhoodId}/amenities")
@Component
public class AmenityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);

    @Autowired
    private AmenityService as;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private String storedETag = ETagUtility.generateETag();
    private Date storedLastModified = getCurrentTimestampForPreconditions();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAmenities(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince,
            @Context Request request) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);

        if (amenities.isEmpty())
            return Response.noContent().build();

        List<AmenityDto> amenitiesDto = amenities.stream()
                .map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        // Use stored ETag value
        EntityTag entityTag = new EntityTag(storedETag);

        CacheControl cacheControl = new CacheControl();

        // Use evaluatePreconditions for efficient conditional requests
        Response.ResponseBuilder builder = request.evaluatePreconditions(
                new Date(storedLastModified.toInstant().toEpochMilli()),
                entityTag);

        if (builder != null) {
            LOGGER.info("Cached");

            // Precondition failed, return 304 Not Modified
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("Fresh");
        // If the cache is not fresh, return the updated data
        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto){})
                .cacheControl(cacheControl)
                .tag(entityTag)
                .header(HttpHeaders.LAST_MODIFIED, storedLastModified)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAmenity(@PathParam("id") final long id,
                                @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                @HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince,
                                @Context Request request) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Fetch Amenity
        Amenity amenity = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new);

        // Use stored ETag value
        EntityTag entityTag = new EntityTag(storedETag);

        CacheControl cacheControl = new CacheControl();

        // Use evaluatePreconditions for efficient conditional requests
        Response.ResponseBuilder builder = request.evaluatePreconditions(
                new Date(storedLastModified.toInstant().toEpochMilli()),
                entityTag);

        if (builder != null) {
            return builder.cacheControl(cacheControl).build();
        }

        // If the cache is not fresh, return the updated data
        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .header(HttpHeaders.LAST_MODIFIED, storedLastModified)
                .build();
    }


    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response createAmenity(@Valid final AmenityForm form) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);
        Amenity amenity = as.createAmenity(form.getName(), form.getDescription(), neighborhoodId, form.getSelectedShifts());

        // Update the ETag and Last-Modified values
        storedETag = ETagUtility.generateETag();
        storedLastModified = ETagUtility.getCurrentTimestampForPreconditions();

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(amenity.getAmenityId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, storedETag)
                .header(HttpHeaders.LAST_MODIFIED, storedLastModified)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateAmenityPartially(
            @PathParam("id") final long id,
            @Valid final AmenityUpdateForm partialUpdate) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        Amenity amenity = as.updateAmenityPartially(id, partialUpdate.getName(), partialUpdate.getDescription());

        // Update the ETag and Last-Modified values
        storedETag = ETagUtility.generateETag();
        storedLastModified = ETagUtility.getCurrentTimestampForPreconditions();

        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .header(HttpHeaders.ETAG, storedETag)
                .header(HttpHeaders.LAST_MODIFIED, storedLastModified)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        if (as.deleteAmenity(id)) {
            // Update the ETag and Last-Modified values
            storedETag = ETagUtility.generateETag();
            storedLastModified = ETagUtility.getCurrentTimestampForPreconditions();

            return Response.noContent()
                    .header(HttpHeaders.ETAG, storedETag)
                    .header(HttpHeaders.LAST_MODIFIED, storedLastModified)
                    .build();
        }

        Response.Status status = Response.Status.NOT_FOUND;
        return Response.status(status).build();
    }
}

