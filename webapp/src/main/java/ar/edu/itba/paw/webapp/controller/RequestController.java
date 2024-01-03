package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RequestService;
import ar.edu.itba.paw.models.Entities.Request;
import ar.edu.itba.paw.webapp.dto.RequestDto;
import ar.edu.itba.paw.webapp.form.RequestForm;
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
public class RequestController {
    @Autowired
    private RequestService rs;

    @Context
    private UriInfo uriInfo;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listRequests(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("userId") @DefaultValue("0") final int userId,
            @QueryParam("productId") @DefaultValue("0") final int productId
            ) {
        List<Request> requests = rs.getRequestsByCriteria(userId, productId, page, size);

        List<RequestDto> requestDto = requests.stream()
                .map(r -> RequestDto.fromRequest(r, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhoods/" + neighborhoodId + "/requests";
        int totalRequestPages = rs.getRequestsCountByCriteria(userId, productId);
        Link[] links = createPaginationLinks(baseUri, page, size, totalRequestPages);

        return Response.ok(new GenericEntity<List<RequestDto>>(requestDto){})
                .links(links)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createRequest(
            @Valid final RequestForm form,
            @QueryParam("productId") @DefaultValue("0") final int productId
    ) {
//        final Request request = rs.createRequest(LoggedUser, productId, form.getRequestMessage());
        final Request request = rs.createRequest(1, productId, form.getRequestMessage());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(request.getRequestId())).build();
        return Response.created(uri).build();
    }
}
