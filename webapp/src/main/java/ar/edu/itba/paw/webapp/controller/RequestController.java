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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/requests")
@Component
public class RequestController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    private final RequestService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private javax.ws.rs.core.Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public RequestController(final UserService us, final RequestService rs) {
        super(us);
        this.rs = rs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessRequests(#productId, #userId)")
    public Response listRequests(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("requestedBy") final Long userId,
            @QueryParam("forProduct") final Long productId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<Request> requests = rs.getRequests(productId, userId, page, size, neighborhoodId);
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
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);

        // Fetch
        Request req = rs.findRequest(requestId, neighborhoodId).orElseThrow(() -> new NotFoundException("Request Not Found"));

        // Check Caching
        EntityTag entityTag = new EntityTag(req.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Fresh Copy
        return Response.ok(RequestDto.fromRequest(req, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canCreateRequest(#productId)")
    public Response createRequest(
            @Valid final RequestForm form,
            @QueryParam("productId") @DefaultValue("0") final int productId
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/requests'", neighborhoodId);

        // Check If-Match Header
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Usual Flow
        final Request request = rs.createRequest(getLoggedUser().getUserId(), productId, form.getRequestMessage());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(request.getRequestId())).build();
        entityLevelETag = ETagUtility.generateETag();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canAccessRequest(#id)")
    public Response updateRequest(
            @PathParam("id") final long requestId,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/requests/{}", neighborhoodId, requestId);

        // Check If-Match header
        if (ifMatch != null){
            String rowVersion = rs.findRequest(requestId, neighborhoodId).orElseThrow(() -> new NotFoundException("Request Not Found")).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, rowVersion)
                        .build();
        }

        // Usual Flow
        rs.markRequestAsFulfilled(requestId);
        final Request request = rs.findRequest(requestId, neighborhoodId)
                .orElseThrow(() -> new NotFoundException("Request Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(request.getRequestId())).build();
        entityLevelETag = ETagUtility.generateETag();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
