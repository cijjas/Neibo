package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.NeighborhoodWorkerService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
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

@Path("/workers/{workerId}/neighborhoods")
@Component
public class NeighborhoodWorkerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborhoodWorkerController.class);

    @Autowired
    private NeighborhoodWorkerService nws;

    @Autowired WorkerService ws;

    @Context
    private UriInfo uriInfo;

    @PathParam("workerId")
    private Long workerId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listWorkersNeighborhoods(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size
    ) {
        LOGGER.info("GET request arrived at workers/{}/neighborhoods", workerId);
        Set<Neighborhood> neighborhoods = nws.getNeighborhoods(workerId);

        List<NeighborhoodDto> neighborhoodsDto = neighborhoods.stream()
                .map(n -> NeighborhoodDto.fromNeighborhood(n, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<NeighborhoodDto>>(neighborhoodsDto){})
                .build();
    }

    @PATCH
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response addWorkerToNeighborhood(@QueryParam("neighborhoodId") final long neighborhoodId) {
        LOGGER.info("PATCH request arrived at  workers/{}/neighborhoods", neighborhoodId);
        nws.addWorkerToNeighborhood(workerId, neighborhoodId);
        Worker worker = ws.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build();
        return Response.created(uri).build();
    }

    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response removeWorkerFromNeighborhood(@QueryParam("neighborhoodId") final long neighborhoodId) {
        LOGGER.info("DELETE request arrived at  workers/{}/neighborhoods", neighborhoodId);
        nws.removeWorkerFromNeighborhood(workerId, neighborhoodId);
        Worker worker = ws.findWorkerById(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build();
        return Response.created(uri).build();
    }
}
