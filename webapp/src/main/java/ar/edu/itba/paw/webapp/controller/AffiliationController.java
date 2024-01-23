package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.Entities.Affiliation;
import ar.edu.itba.paw.webapp.dto.AffiliationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("/affiliations")
@Component
public class AffiliationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationController.class);

    @Autowired
    private NeighborhoodWorkerService nws;

    @Autowired WorkerService ws;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listAffiliations(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("inNeighborhood") Long neighborhoodId,
            @QueryParam("forWorker") Long workerId
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        Set<Affiliation> affiliations = nws.getAffiliations(workerId, neighborhoodId, page, size);

        if (affiliations.isEmpty())
            return Response.noContent().build();

        List<AffiliationDto> affiliationDto = affiliations.stream()
                .map(wa -> AffiliationDto.fromAffiliation(wa, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "affiliations/";
        int totalAmenityPages = nws.calculateAffiliationPages(workerId, neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);
        return Response.ok(new GenericEntity<List<AffiliationDto>>(affiliationDto){})
                .links(links)
                .build();
    }

    @PATCH
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response addWorkerToNeighborhood(
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("worker") Long workerId
    ) {
        LOGGER.info("PATCH request arrived at '/workers/{}/neighborhoods'", neighborhoodId);
        nws.addWorkerToNeighborhood(workerId, neighborhoodId);
        Worker worker = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response removeWorkerFromNeighborhood(
            @QueryParam("toNeighborhood") Long neighborhoodId,
            @QueryParam("worker") Long workerId
    ) {
        LOGGER.info("DELETE request arrived at '/workers/{}/neighborhoods'", neighborhoodId);
        nws.removeWorkerFromNeighborhood(workerId, neighborhoodId);
        Worker worker = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build();
        return Response.created(uri).build();
    }
}
