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
import java.util.List;
import java.util.stream.Collectors;

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

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listAmenities(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        // Check Caching
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);
        if (amenities.isEmpty())
            return Response.noContent().build();
        List<AmenityDto> amenitiesDto = amenities.stream().map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto) {
                })
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findAmenity(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
            @Context Request request
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Fetch Amenity
        Amenity amenity = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new);
        // Use stored ETag value
        EntityTag entityTag = new EntityTag(amenity.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        // Client has a valid version
        if (builder != null)
            return builder.cacheControl(cacheControl).build();
        // Client has an invalid version
        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }


    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_ADMINISTRATOR")
    public Response createAmenity(
            @Valid final AmenityForm form,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
            @Context Request request
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        if (ifMatch != null) {
            Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .entity("Your cached version of the resource is outdated.")
                        .header(HttpHeaders.ETAG, entityLevelETag)
                        .build();
        }

        Amenity amenity = as.createAmenity(form.getName(), form.getDescription(), neighborhoodId, form.getSelectedShifts());
        entityLevelETag = ETagUtility.generateETag();
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(amenity.getAmenityId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_ADMINISTRATOR")
    public Response updateAmenityPartially(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
            @Valid final AmenityUpdateForm partialUpdate,
            @Context Request request
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Check If-Match header
        if (ifMatch != null) {
            String version = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        Amenity amenity = as.updateAmenityPartially(id, partialUpdate.getName(), partialUpdate.getDescription());
        entityLevelETag = ETagUtility.generateETag();
        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch,
            @Context Request request
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        if (ifMatch != null) {
            String version = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        if (as.deleteAmenity(id)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
