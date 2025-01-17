package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.dto.RequestsCountDto;
import ar.edu.itba.paw.webapp.dto.queryForms.RequestForm;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
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
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.*;

/*
 * # Summary
 *   - A single Product can receive many Requests
 *
 * # Use cases
 *   - A User makes one or more Requests for one or more Products, any of those can be fulfilled or negated
 *   - The Seller can list the Requests he has received for a certain Product
 *   - A User can list the Requests he has made for certain Products
 */

@Path(Endpoint.NEIGHBORHOODS + "/{" + PathParameter.NEIGHBORHOOD_ID + "}/" + Endpoint.REQUESTS)
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
    @PreAuthorize("@pathAccessControlHelper.canAccessRequests(#requestForm.requestedBy, #requestForm.forProduct)")
    public Response listRequests(
            @Valid @BeanParam RequestForm requestForm
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests'", requestForm.getNeighborhoodId());

        // ID Extraction
        Long userId = extractOptionalSecondId(requestForm.getRequestedBy());
        Long productId = extractOptionalSecondId(requestForm.getForProduct());
        Long requestStatusId = extractOptionalFirstId(requestForm.getWithStatus());
        Long transactionTypeId = extractOptionalFirstId(requestForm.getWithType());

        // Content
        final List<Request> requests = rs.getRequests(requestForm.getNeighborhoodId(), userId, productId, requestStatusId, transactionTypeId,
                requestForm.getPage(), requestForm.getSize());
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
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.NEIGHBORHOODS).path(String.valueOf(requestForm.getNeighborhoodId())).path(Endpoint.REQUESTS),
                rs.calculateRequestPages(requestForm.getNeighborhoodId(), userId, productId, requestStatusId, transactionTypeId,
                        requestForm.getSize()),
                requestForm.getPage(),
                requestForm.getSize()
        );

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto) {
                })
                .links(links)
                .tag(requestsHashCode)
                .cacheControl(cacheControl)
                .build();
    }

    @GET
    @Path(Endpoint.COUNT)
    public Response countRequests(
            @Valid @BeanParam RequestForm requestForm
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/count'", requestForm.getNeighborhoodId());

        // ID Extraction
        Long userId = extractOptionalSecondId(requestForm.getRequestedBy());
        Long productId = extractOptionalSecondId(requestForm.getForProduct());
        Long requestStatusId = extractOptionalFirstId(requestForm.getWithStatus());
        Long transactionTypeId = extractOptionalFirstId(requestForm.getWithType());

        // Content
        int count = rs.countRequests(requestForm.getNeighborhoodId(), userId, productId, requestStatusId, transactionTypeId);
        String countHashCode = String.valueOf(count);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(countHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        RequestsCountDto dto = RequestsCountDto.fromRequestsCount(count, requestForm.getNeighborhoodId(), requestForm.getRequestedBy(), requestForm.getForProduct(), requestForm.getWithStatus(), requestForm.getWithType(), uriInfo);

        return Response.ok(new GenericEntity<RequestsCountDto>(dto) {
                })
                .cacheControl(cacheControl)
                .tag(countHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @PreAuthorize("@pathAccessControlHelper.canAccessRequest(#requestId)")
    public Response findRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) long requestId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

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
    @Validated(CreateValidationSequence.class)
    public Response createRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @Valid @NotNull RequestDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Creation & HashCode Generation
        final Request request = rs.createRequest(extractFirstId(createForm.getUser()), extractSecondId(createForm.getProduct()), createForm.getMessage(), createForm.getUnitsRequested());
        String requestHashCode = String.valueOf(request.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(request.getRequestId())).build();

        return Response.created(uri)
                .tag(requestHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @Consumes(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@pathAccessControlHelper.canUpdateRequest(#requestId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) @GenericIdConstraint long requestId,
            @Valid @NotNull RequestDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/requests/{}", neighborhoodId, requestId);

        // Modification & HashCode Generation
        final Request updatedRequest = rs.updateRequest(neighborhoodId, requestId, extractOptionalFirstId(updateForm.getRequestStatus()));
        String requestHashCode = String.valueOf(updatedRequest.hashCode());

        return Response.ok(RequestDto.fromRequest(updatedRequest, uriInfo))
                .tag(requestHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.REQUEST_ID + "}")
    @PreAuthorize("@pathAccessControlHelper.canDeleteRequest(#requestId)")
    public Response deleteRequest(
            @PathParam(PathParameter.NEIGHBORHOOD_ID) @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam(PathParameter.REQUEST_ID) @GenericIdConstraint long requestId
    ) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Deletion Attempt
        if (rs.deleteRequest(neighborhoodId, requestId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
