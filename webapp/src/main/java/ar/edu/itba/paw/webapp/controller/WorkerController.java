package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReviewService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.form.WorkerUpdateForm;
import ar.edu.itba.paw.webapp.form.WorkerSignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkModificationETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkMutableETagPreconditions;

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
public class WorkerController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private WorkerService ws;

    @Autowired
    private ReviewService rs;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_WORKER", "ROLE_SUPER_ADMINISTRATOR"})
    public Response listWorkers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withProfessions") final List<String> professions,
            @QueryParam("inNeighborhoods") final List<String> neighborhoods,
            @QueryParam("withRole") final String workerRole,
            @QueryParam("withStatus") final String workerStatus
    ) {
        LOGGER.info("GET request arrived at '/workers'");

        // Content
        List<Worker> workers = ws.getWorkers(page, size, professions, neighborhoods, workerRole, workerStatus);
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
                .map(w -> WorkerDto.fromWorker(w, rs.getAvgRating(w.getWorkerId()), uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "workers",
                ws.calculateWorkerPages(professions, neighborhoods, size, workerRole, workerStatus),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<WorkerDto>>(workerDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(workersHashCode)
                .build();
    }


    @GET
    @Path("/{id}")
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_WORKER", "ROLE_SUPER_ADMINISTRATOR"})
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findWorker(
            @PathParam("id") final long workerId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/workers/{}'", workerId);

        // Content
        Worker worker = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker not found"));
        String workerHashCode = String.valueOf(worker.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerDto.fromWorker(worker, rs.getAvgRating(worker.getWorkerId()), uriInfo))
                .cacheControl(cacheControl)
                .tag(workerHashCode)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createWorker(
            @Valid @NotNull final WorkerSignupForm form
    ) {
        LOGGER.info("POST request arrived at '/workers'");

        // Creation & Etag Generation
        final Worker worker = ws.createWorker(form.getUser(), form.getPhoneNumber(), form.getAddress(), form.getProfessions(), form.getBusinessName());
        String workerHashCode = String.valueOf(worker.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(worker.getWorkerId())).build();

        return Response.created(uri)
                .tag(workerHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canUpdateWorker(#workerId)")
    public Response updateWorkerPartially(
            @PathParam("id") final long workerId,
            @Valid @NotNull final WorkerUpdateForm partialUpdate
    ) {
        LOGGER.info("PATCH request arrived at '/workers/{}'", workerId);

        // Modification & HashCode Generation
        final Worker updatedWorker = ws.updateWorkerPartially(workerId, partialUpdate.getPhoneNumber(), partialUpdate.getAddress(), partialUpdate.getBusinessName(), partialUpdate.getBackgroundPicture(), partialUpdate.getBio());
        String workerHashCode = String.valueOf(updatedWorker.hashCode());

        return Response.ok(WorkerDto.fromWorker(updatedWorker, rs.getAvgRating(updatedWorker.getWorkerId()), uriInfo))
                .tag(workerHashCode)
                .build();
    }

/*    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteById(@PathParam("id") final long workerId) {
        LOGGER.info("DELETE request arrived at '/workers/{}", workerId);

        // Deletion Attempt
        if(ws.deleteWorker(workerId)) {
            return Response.noContent()
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }*/
}
