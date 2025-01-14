package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.controller.constants.*;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.NeighborhoodsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ProfessionsURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.WorkerRoleURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.WorkerStatusURNConstraint;
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
 *   - A client can register as a Worker
 *   - A Worker can update his/her profile
 *   - A User/Admin/Worker can list the Workers in a Neighborhood
 */

@Path(Endpoint.WORKERS)
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
    @Secured({UserRole.WORKER, UserRole.NEIGHBOR, UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    public Response listWorkers(
            @QueryParam(QueryParameter.IN_NEIGHBORHOOD) @NeighborhoodsURNConstraint List<String> neighborhoods,
            @QueryParam(QueryParameter.WITH_PROFESSION) @ProfessionsURNConstraint List<String> professions,
            @QueryParam(QueryParameter.WITH_ROLE) @WorkerRoleURNConstraint String workerRole,
            @QueryParam(QueryParameter.WITH_STATUS) @WorkerStatusURNConstraint String workerStatus,
            @QueryParam(QueryParameter.PAGE) @DefaultValue(Constant.DEFAULT_PAGE) int page,
            @QueryParam(QueryParameter.SIZE) @DefaultValue(Constant.DEFAULT_SIZE) int size
    ) {
        LOGGER.info("GET request arrived at '/workers'");

        // ID Extraction
        List<Long> neighborhoodIds = extractFirstIds(neighborhoods);
        List<Long> professionIds = extractFirstIds(professions);
        Long workerRoleId = extractOptionalFirstId(workerRole);
        Long workerStatusId = extractOptionalFirstId(workerStatus);

        // Content
        List<Worker> workers = ws.getWorkers(neighborhoodIds, professionIds, workerRoleId, workerStatusId, page, size);
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
                uriInfo.getBaseUriBuilder().path(Endpoint.WORKERS),
                ws.calculateWorkerPages(neighborhoodIds, professionIds, workerRoleId, workerStatusId, size),
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
    @Path("{" + PathParameter.WORKER_ID + "}")
    @Secured({UserRole.WORKER, UserRole.NEIGHBOR, UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    public Response findWorker(
            @PathParam(PathParameter.WORKER_ID) @GenericIdConstraint long workerId
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

        return Response.ok(WorkerDto.fromWorker(worker, uriInfo))
                .cacheControl(cacheControl)
                .tag(workerHashCode)
                .build();
    }

    @POST
    @Validated(CreateValidationSequence.class)
    public Response createWorker(
            @Valid @NotNull WorkerDto createForm
    ) {
        LOGGER.info("POST request arrived at '/workers'");

        // Creation & Etag Generation
        final Worker worker = ws.createWorker(extractSecondId(createForm.getUser()), extractFirstIds(createForm.getProfessions()), createForm.getBusinessName(), createForm.getAddress(), createForm.getPhoneNumber());
        String workerHashCode = String.valueOf(worker.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(worker.getWorkerId())).build();

        return Response.created(uri)
                .tag(workerHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.WORKER_ID + "}")
    @PreAuthorize("@pathAccessControlHelper.canUpdateWorker(#workerId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateWorker(
            @PathParam(PathParameter.WORKER_ID) @GenericIdConstraint long workerId,
            @Valid @NotNull WorkerDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/workers/{}'", workerId);

        // Modification & HashCode Generation
        final Worker updatedWorker = ws.updateWorker(workerId, updateForm.getBusinessName(), updateForm.getAddress(), updateForm.getPhoneNumber(), extractOptionalFirstId(updateForm.getBackgroundPicture()), updateForm.getBio());
        String workerHashCode = String.valueOf(updatedWorker.hashCode());

        return Response.ok(WorkerDto.fromWorker(updatedWorker, uriInfo))
                .tag(workerHashCode)
                .build();
    }
}
