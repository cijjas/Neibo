package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.form.WorkerUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("/workers")
@Component
public class WorkerController {

    @Autowired
    private WorkerService ws;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listWorkers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("professions") final List<String> professions,
            @QueryParam("neighborhoodId") @DefaultValue("0") final long neighborhoodId,
            @QueryParam("loggedUserId") final long loggedUserId,
            @QueryParam("workerRole") final WorkerRole workerRole,
            @QueryParam("workerStatus") final WorkerStatus workerStatus
    ) {
        Set<Worker> workers = ws.getWorkersByCriteria(page, size, professions, neighborhoodId, loggedUserId, workerRole, workerStatus);

        String baseUri;
        if (neighborhoodId != 0) {
            baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/workers";
        } else {
            baseUri = uriInfo.getBaseUri().toString() + "workers";
        }

        int totalWorkerPages = ws.getTotalWorkerPagesByCriteria(professions, new long[]{neighborhoodId}, size, workerRole, workerStatus);
        Link[] links = createPaginationLinks(baseUri, page, size, totalWorkerPages);

        List<WorkerDto> workerDto = workers.stream()
                .map(w -> WorkerDto.fromWorker(w, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerDto>>(workerDto){})
                .links(links)
                .build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findAmenity(@PathParam("id") final long id) {
        Optional<Worker> worker = ws.findWorkerById(id);
        if (!worker.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(WorkerDto.fromWorker(worker.get(), uriInfo)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateWorkerPartially(
            @PathParam("id") final long id,
            @Valid final WorkerUpdateForm partialUpdate) {
        final Worker worker = ws.updateWorkerPartially(id, partialUpdate.getPhoneNumber(), partialUpdate.getAddress(), partialUpdate.getBusinessName(), partialUpdate.getBackgroundPicture(), partialUpdate.getBio());
        return Response.ok(WorkerDto.fromWorker(worker, uriInfo)).build();
    }
}
