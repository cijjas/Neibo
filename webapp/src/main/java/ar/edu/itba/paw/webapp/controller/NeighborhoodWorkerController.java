package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.models.Entities.WorkerArea;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import ar.edu.itba.paw.webapp.dto.WorkerAreaDto;
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
public class NeighborhoodWorkerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerController.class);

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
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("workerId") Long workerId
    ) {
        LOGGER.info("GET request arrived at '/affiliations'");

        Set<WorkerArea> workerAreas = nws.getAffiliations(workerId, neighborhoodId, page, size);

        if (workerAreas.isEmpty())
            return Response.noContent().build();

        List<WorkerAreaDto> workerAreaDto = workerAreas.stream()
                .map(wa -> WorkerAreaDto.fromWorkerArea(wa, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "affiliations/";
        int totalAmenityPages = nws.calculateAffiliationPages(workerId, neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);
        return Response.ok(new GenericEntity<List<WorkerAreaDto>>(workerAreaDto){})
                .links(links)
                .build();
    }

    @PATCH
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response addWorkerToNeighborhood(
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("workerId") Long workerId
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
            @QueryParam("neighborhoodId") Long neighborhoodId,
            @QueryParam("workerId") Long workerId
    ) {
        LOGGER.info("DELETE request arrived at '/workers/{}/neighborhoods'", neighborhoodId);
        nws.removeWorkerFromNeighborhood(workerId, neighborhoodId);
        Worker worker = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build();
        return Response.created(uri).build();
    }
}
