package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.NeighborhoodsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.ProfessionsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerRoleURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
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
 *   - A client can register as a Worker
 *   - A Worker can update his/her profile
 *   - A User/Admin/Worker can list the Workers in a Neighborhood
 */

@Path("/workers")
@Component
@Validated
public class WorkerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final ReviewService rs;
    private final WorkerService ws;

    @Autowired
    public WorkerController(ReviewService rs, WorkerService ws) {
        this.rs = rs;
        this.ws = ws;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_WORKER", "ROLE_SUPER_ADMINISTRATOR"})
    public Response listWorkers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withProfessions") @ProfessionsURNConstraint final List<String> professions,
            @QueryParam("inNeighborhoods") @NeighborhoodsURNConstraint final List<String> neighborhoods,
            @QueryParam("withRole") @WorkerRoleURNConstraint final String workerRole,
            @QueryParam("withStatus") @WorkerStatusURNConstraint final String workerStatus
    ) {
        LOGGER.info("GET request arrived at '/workers'");

        // ID Extraction
        List<Long> professionIds = extractSecondIds(professions);
        List<Long> neighborhoodIds = extractSecondIds(neighborhoods);
        Long workerRoleId = extractOptionalFirstId(workerRole);
        Long workerStatusId = extractOptionalFirstId(workerRole);

        // Content
        List<Worker> workers = ws.getWorkers(page, size, professionIds, neighborhoodIds, workerRoleId, workerStatusId);
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
                .map(w -> WorkerDto.fromWorker(w, rs.findAverageRating(w.getWorkerId()), uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "/workers",
                ws.calculateWorkerPages(professionIds, neighborhoodIds, size, workerRoleId, workerStatusId),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<WorkerDto>>(workerDto) {
                })
                .links(links)
                .cacheControl(cacheControl)
                .tag(workersHashCode)
                .build();
    }


    @GET
    @Path("/{id}")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_WORKER", "ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response findWorker(
            @PathParam("id") @GenericIdConstraint final long workerId
    ) {
        LOGGER.info("GET request arrived at '/workers/{}'", workerId);

        // Content
        Worker worker = ws.findWorker(workerId).orElseThrow(NotFoundException::new);
        String workerHashCode = String.valueOf(worker.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerDto.fromWorker(worker, rs.findAverageRating(worker.getWorkerId()), uriInfo))
                .cacheControl(cacheControl)
                .tag(workerHashCode)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(CreateValidationSequence.class)
    public Response createWorker(
            @Valid WorkerDto form
    ) {
        LOGGER.info("POST request arrived at '/workers'");

        // Creation & Etag Generation
        final Worker worker = ws.createWorker(extractSecondId(form.getUser()), form.getPhoneNumber(), form.getAddress(), extractFirstIds(form.getProfessions()), form.getBusinessName());
        String workerHashCode = String.valueOf(worker.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(worker.getWorkerId())).build();

        return Response.created(uri)
                .tag(workerHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@pathAccessControlHelper.canUpdateWorker(#workerId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateWorkerPartially(
            @PathParam("id") @GenericIdConstraint final long workerId,
            @Valid WorkerDto partialUpdate
    ) {
        LOGGER.info("PATCH request arrived at '/workers/{}'", workerId);

        // Modification & HashCode Generation
        final Worker updatedWorker = ws.updateWorkerPartially(workerId, partialUpdate.getPhoneNumber(), partialUpdate.getAddress(), partialUpdate.getBusinessName(), extractOptionalFirstId(partialUpdate.getBackgroundPicture()), partialUpdate.getBio());
        String workerHashCode = String.valueOf(updatedWorker.hashCode());

        return Response.ok(WorkerDto.fromWorker(updatedWorker, rs.findAverageRating(updatedWorker.getWorkerId()), uriInfo))
                .tag(workerHashCode)
                .build();
    }
}
