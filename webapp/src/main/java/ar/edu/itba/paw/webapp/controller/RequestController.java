package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.dto.PurchaseDto;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.form.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @Autowired
    public RequestController(final UserService us, final RequestService rs) {
        super(us);
        this.rs = rs;
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listRequests(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("userId") final Long userId,
            @QueryParam("productId") final Long productId
            ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests'", neighborhoodId);
        List<Request> requests = rs.getRequests(userId, productId, page, size, neighborhoodId);

        List<RequestDto> requestDto = requests.stream()
                .map(r -> RequestDto.fromRequest(r, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/requests";
        int totalRequestPages = rs.calculateRequestPages(userId, productId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalRequestPages);

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findRequest(@PathParam("id") final long requestId) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/requests/{}'", neighborhoodId, requestId);
        return Response.ok(RequestDto.fromRequest(rs.findRequest(requestId, neighborhoodId)
                .orElseThrow(() -> new NotFoundException("Request Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createRequest(
            @Valid final RequestForm form,
            @QueryParam("productId") @DefaultValue("0") final int productId
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/requests'", neighborhoodId);
        final Request request = rs.createRequest(getLoggedUser().getUserId(), productId, form.getRequestMessage());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(request.getRequestId())).build();
        return Response.created(uri).build();
    }
}
