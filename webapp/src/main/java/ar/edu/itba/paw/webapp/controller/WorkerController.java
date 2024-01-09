package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.form.WorkerUpdateForm;
import ar.edu.itba.paw.webapp.form.WorkerSignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
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

        String baseUri = uriInfo.getBaseUri().toString() + "workers";

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
    public Response findWorker(@PathParam("id") final long id) {
        Optional<Worker> worker = ws.findWorkerById(id);
        if (!worker.isPresent()) {
            throw new NotFoundException("Worker Not Found");
        }
        return Response.ok(WorkerDto.fromWorker(worker.get(), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createWorker(@Valid final WorkerSignupForm form) {
        final Worker worker = ws.createWorker(form.getW_mail(), form.getW_name(), form.getW_surname(), form.getW_password(), form.getW_identification(), form.getPhoneNumber(), form.getAddress(), Language.ENGLISH, form.getProfessionIds(), form.getBusinessName());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(worker.getWorkerId())).build(); //esto esta bien o es el userId que necesita??
        return Response.created(uri).build();
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
