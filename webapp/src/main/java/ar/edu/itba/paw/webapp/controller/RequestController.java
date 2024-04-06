package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.form.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.*;

@Path("neighborhoods/{neighborhoodId}/requests")
@Component
public class RequestController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private javax.ws.rs.core.Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessRequests(#userId, #productId)")
    public Response listRequests(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("requestedBy") final Long userId,
            @QueryParam("forProduct") final Long productId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Request> requests = rs.getRequests(userId, productId, page, size, neighborhoodId);
        if (requests.isEmpty())
            return Response.noContent().build();
        List<RequestDto> requestDto = requests.stream()
                .map(r -> RequestDto.fromRequest(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/requests",
                rs.calculateRequestPages(productId, userId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessRequest(#requestId)")
    public Response findRequest(
            @PathParam("id") final long requestId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Content
        Request request = rs.findRequest(requestId, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(request.getVersion().toString());
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        return Response.ok(RequestDto.fromRequest(request, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canCreateRequest(#form.productURN)")
    public Response createRequest(
            @Valid @NotNull final RequestForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & Etag Generation
        final Request request = rs.createRequest(getLoggedUserId(), form.getProductURN(), form.getRequestMessage());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(request.getVersion().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(request.getRequestId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessRequest(#id)")
    public Response updateRequest(
            @PathParam("id") final long requestId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/requests/{}", neighborhoodId, requestId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(rs.findRequest(requestId, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Modification & ETag Generation
        final Request updatedRequest = rs.markRequestAsFulfilled(requestId);
        entityLevelETag = ETagUtility.generateETag();
        rowLevelETag = new EntityTag(updatedRequest.getVersion().toString());

        return Response.ok(RequestDto.fromRequest(updatedRequest, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteRequest(#requestId)")
    public Response deleteById(
            @PathParam("id") final long requestId,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(rs.findRequest(requestId, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Deletion & ETag Generation Attempt
        if (rs.deleteRequest(requestId)) {
            entityLevelETag = ETagUtility.generateETag();
            return Response.noContent()
                    .tag(entityLevelETag)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }
}
