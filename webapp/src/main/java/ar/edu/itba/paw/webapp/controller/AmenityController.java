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

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/amenities")
@Component
public class AmenityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);

    @Autowired
    private AmenityService as;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response listAmenities(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Amenity> amenities = as.getAmenities(neighborhoodId, page, size);
        if (amenities.isEmpty())
            return Response.noContent().build();
        List<AmenityDto> amenitiesDto = amenities.stream().map(a -> AmenityDto.fromAmenity(a, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/amenities",
                as.calculateAmenityPages(neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<AmenityDto>>(amenitiesDto) {})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findAmenity(
            @PathParam("id") final long id
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Content
        Amenity amenity = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(amenity.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(AmenityDto.fromAmenity(amenity, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }


    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_ADMINISTRATOR")
    public Response createAmenity(
            @Valid final AmenityForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/amenities'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & ETag Generation
        Amenity amenity = as.createAmenity(form.getName(), form.getDescription(), neighborhoodId, form.getSelectedShifts());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
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
            @Valid final AmenityUpdateForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Cache Control
        if (ifMatch != null){
            String rowVersion = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, rowVersion)
                        .build();
        }

        // Modification & ETag Generation
        AmenityDto amenityDto = AmenityDto.fromAmenity(as.updateAmenityPartially(id, partialUpdate.getName(), partialUpdate.getDescription()), uriInfo);
        entityLevelETag = ETagUtility.generateETag();

        return Response.ok(amenityDto)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured("ROLE_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("id") final long id,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/amenities/{}'", neighborhoodId, id);

        // Cache Control
        if (ifMatch != null) {
            String rowVersion = as.findAmenity(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, rowVersion)
                        .build();
        }

        // Deletion & ETag Generation Attempt
        if (as.deleteAmenity(id)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
