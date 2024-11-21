package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProductURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.TransactionTypeURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNFormConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.ProductURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.RequestStatusURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.TransactionTypeURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.reference.UserURNReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ValidationUtils.*;

/*
 * # Summary
 *   - A single Product can receive many Requests
 *
 * # Use cases
 *   - A User makes one or more Requests for one or more Products, any of those can be fulfilled or negated
 *   - The Seller can list the Requests he has received for a certain Product
 *   - A User can list the Requests he has made for certain Products
 */

@Path("neighborhoods/{neighborhoodId}/requests")
@Component
@Validated
public class RequestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private javax.ws.rs.core.Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canAccessRequests(#userURN, #productId)")
    public Response listRequests(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("requestedBy") @UserURNFormConstraint @UserURNReferenceConstraint final String user,
            @QueryParam("forProduct") @ProductURNFormConstraint @ProductURNReferenceConstraint final String product,
            @QueryParam("withType") @TransactionTypeURNFormConstraint @TransactionTypeURNReferenceConstraint final String type,
            @QueryParam("withStatus") @RequestStatusURNFormConstraint @RequestStatusURNReferenceConstraint final String status
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // ID Extraction
        Long userId = extractOptionalSecondId(user);
        Long productId = extractOptionalSecondId(product);
        Long transactionTypeId = extractOptionalFirstId(type);
        Long requestStatusId = extractOptionalFirstId(status);

        // Content
        final List<Request> requests = rs.getRequests(userId, productId, transactionTypeId, requestStatusId, page, size, neighborhoodId);
        String requestsHashCode = String.valueOf(requests.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(requestsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (requests.isEmpty())
            return Response.noContent()
                    .tag(requestsHashCode)
                    .build();

        final List<RequestDto> requestDto = requests.stream()
                .map(r -> RequestDto.fromRequest(r, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/requests",
                rs.calculateRequestPages(productId, userId, transactionTypeId, requestStatusId, neighborhoodId, size),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto) {})
                .links(links)
                .tag(requestsHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canAccessRequest(#requestId)")
    public Response findRequest(
            @PathParam("id") final long requestId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Content
        Request productRequest = rs.findRequest(requestId, neighborhoodId).orElseThrow(() -> new NotFoundException("Request not found"));
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createRequest(
            @Valid RequestDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Creation & HashCode Generation
        final Request request = rs.createRequest(extractSecondId(form.getUser()), extractSecondId(form.getProduct()), form.getRequestMessage(), form.getUnits());
        String requestHashCode = String.valueOf(request.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(request.getRequestId())).build();

        return Response.created(uri)
                .tag(requestHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateRequest(#requestId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateRequest(
            @PathParam("id") final long requestId,
            @Valid RequestDto form
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/requests/{}", neighborhoodId, requestId);

        // Modification & HashCode Generation
        final Request updatedRequest = rs.updateRequest(requestId, extractOptionalFirstId(form.getRequestStatus()));
        String requestHashCode = String.valueOf(updatedRequest.hashCode());

        return Response.ok(RequestDto.fromRequest(updatedRequest, uriInfo))
                .tag(requestHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canDeleteRequest(#requestId)")
    public Response deleteById(
            @PathParam("id") final long requestId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Deletion Attempt
        if (rs.deleteRequest(requestId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
