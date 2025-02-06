package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.dto.queryForms.WorkerParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
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
 *   - Main entity for the Workers functionality
 *
 * # Use Cases
 *   - Anyone can register as a Worker
 *   - A Worker can update his/her profile
 *   - A Worker can list all the Workers
 *   - A Neighbor/Admin can list the Workers affiliated with their Neighborhood
 */

@Path(Endpoint.API + "/" + Endpoint.WORKERS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class WorkerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerController.class);
    private final WorkerService ws;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public WorkerController(WorkerService ws) {
        this.ws = ws;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListWorkers(#workerParams.neighborhoods, #workerParams.professions, #workerParams.workerRole, #workerParams.workerStatus)")
    public Response listWorkers(
            @Valid @BeanParam WorkerParams workerParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        List<Long> neighborhoodIds = extractFirstIds(workerParams.getNeighborhoods());
        List<Long> professionIds = extractFirstIds(workerParams.getProfessions());
        Long workerRoleId = extractOptionalFirstId(workerParams.getWorkerRole());
        Long workerStatusId = extractOptionalFirstId(workerParams.getWorkerStatus());

        // Content
        List<Worker> workers = ws.getWorkers(neighborhoodIds, professionIds, workerRoleId, workerStatusId, workerParams.getPage(), workerParams.getSize());
        String workersHashCode = String.valueOf(workers.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workersHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (workers.isEmpty())
            return Response.noContent()
                    .tag(workersHashCode)
                    .build();

        List<WorkerDto> workerDto = workers.stream()
                .map(w -> WorkerDto.fromWorker(w, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS),
                ws.calculateWorkerPages(neighborhoodIds, professionIds, workerRoleId, workerStatusId, workerParams.getSize()),
                workerParams.getPage(),
                workerParams.getSize()
        );

        return Response.ok(new GenericEntity<List<WorkerDto>>(workerDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(workersHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.WORKER_ID + "}")
    public Response findWorker(
            @PathParam(PathParameter.WORKER_ID) long workerId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        Worker worker = ws.findWorker(workerId).orElseThrow(NotFoundException::new);
        String workerHashCode = String.valueOf(worker.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerDto.fromWorker(worker, uriInfo))
                .cacheControl(cacheControl)
                .tag(workerHashCode)
                .build();
    }

    @POST
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateWorker(#createForm.user, #createForm.professions, #createForm.backgroundImage)")
    public Response createWorker(
            @Valid @NotNull WorkerDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & Etag Generation
        final Worker worker = ws.createWorker(extractFirstId(createForm.getUser()), extractFirstIds(createForm.getProfessions()), createForm.getBusinessName(), createForm.getAddress(), createForm.getPhoneNumber());
        String workerHashCode = String.valueOf(worker.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(worker.getWorkerId())).build();

        return Response.created(uri)
                .tag(workerHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.WORKER_ID + "}")
    @PreAuthorize("@accessControlHelper.canUpdateWorker(#updateForm.user, #updateForm.professions, #updateForm.backgroundImage, #workerId)")
    @Validated(UpdateSequence.class)
    public Response updateWorker(
            @PathParam(PathParameter.WORKER_ID) long workerId,
            @Valid @NotNull WorkerDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final Worker updatedWorker = ws.updateWorker(workerId, updateForm.getBusinessName(), updateForm.getAddress(), updateForm.getPhoneNumber(), extractOptionalFirstId(updateForm.getBackgroundImage()), updateForm.getBio());
        String workerHashCode = String.valueOf(updatedWorker.hashCode());

        return Response.ok(WorkerDto.fromWorker(updatedWorker, uriInfo))
                .tag(workerHashCode)
                .build();
    }
}
