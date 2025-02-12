package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.dto.queryForms.RequestParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.COUNT_HEADER;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - A single Product can receive many Requests
 *
 * # Use cases
 *   - A Neighbor can make a Request for a Product
 *   - The Seller can accept or deny the Requests
 *   - The Seller can list the Requests he has received for a certain Product
 *   - A User can list the Requests he has made for certain Products
 */

@Path(Endpoint.API + "/" + Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.REQUESTS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON,})
public class RequestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);
    private final RequestService rs;
    @Context
    private UriInfo uriInfo;
    @Context
    private javax.ws.rs.core.Request request;

    @Autowired
    public RequestController(RequestService rs) {
        this.rs = rs;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListOrCountRequests(#requestParams.user, #requestParams.product, #requestParams.transactionType, #requestParams.requestStatus)")
    public Response listRequests(
            @Valid @BeanParam RequestParams requestParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long userId = extractNullableFirstId(requestParams.getUser());
        Long productId = extractNullableSecondId(requestParams.getProduct());
        Long transactionTypeId = extractNullableFirstId(requestParams.getTransactionType());
        Long requestStatusId = extractNullableFirstId(requestParams.getRequestStatus());

        // Content
        final List<Request> requests = rs.getRequests(requestParams.getNeighborhoodId(), userId, productId, transactionTypeId, requestStatusId,
                requestParams.getPage(), requestParams.getSize());
        String requestsHashCode = String.valueOf(requests.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestsHashCode));
        if (builder != null) {
            return builder.cacheControl(cacheControl).build();
        }

        if (requests.isEmpty()) {
            return Response.noContent()
                    .tag(requestsHashCode)
                    .build();
        }

        final List<RequestDto> requestDto = requests.stream()
                .map(r -> RequestDto.fromRequest(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        int requestsCount = rs.countRequests(requestParams.getNeighborhoodId(), userId, productId, transactionTypeId, requestStatusId);
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(String.valueOf(requestParams.getNeighborhoodId())).path(Endpoint.REQUESTS),
                requestsCount,
                requestParams.getPage(),
                requestParams.getSize()
        );

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto) {
                })
                .links(links)
                .tag(requestsHashCode)
                .cacheControl(cacheControl)
                .header(COUNT_HEADER, requestsCount)
                .build();
    }

    @GET
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @PreAuthorize("@accessControlHelper.canFindRequest(#neighborhoodId, #requestId)")
    public Response findRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) long requestId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Request productRequest = rs.findRequest(neighborhoodId, requestId).orElseThrow(NotFoundException::new);
        String requestHashCode = String.valueOf(productRequest.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(RequestDto.fromRequest(productRequest, uriInfo))
                .cacheControl(cacheControl)
                .tag(requestHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateRequest(#createForm.user, #createForm.product)")
    public Response createRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @Valid @NotNull RequestDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & HashCode Generation
        final Request request = rs.createRequest(neighborhoodId, extractFirstId(createForm.getUser()), extractSecondId(createForm.getProduct()), createForm.getMessage(), createForm.getUnitsRequested());
        String requestHashCode = String.valueOf(request.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(request.getRequestId())).build();

        return Response.created(uri)
                .tag(requestHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateRequest(#updateForm.user, #updateForm.product, #updateForm.requestStatus, #neighborhoodId, #requestId)")
    @Validated(UpdateSequence.class)
    public Response updateRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) long requestId,
            @Valid @NotNull RequestDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Request updatedRequest = rs.updateRequest(neighborhoodId, requestId, extractNullableFirstId(updateForm.getRequestStatus()));
        String requestHashCode = String.valueOf(updatedRequest.hashCode());

        return Response.ok(RequestDto.fromRequest(updatedRequest, uriInfo))
                .tag(requestHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @PreAuthorize("@accessControlHelper.canDeleteRequest(#neighborhoodId, #requestId)")
    public Response deleteRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) long requestId
    ) {
        LOGGER.info("DELETE request arrived at '{}'", uriInfo.getRequestUri());

        // Deletion Attempt
        if (rs.deleteRequest(neighborhoodId, requestId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
