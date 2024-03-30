package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Worker;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.WorkerDto;
import ar.edu.itba.paw.webapp.form.WorkerUpdateForm;
import ar.edu.itba.paw.webapp.form.WorkerSignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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
public class WorkerController extends GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private WorkerService ws;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @Autowired
    public WorkerController(final UserService us) {
        super(us);
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured({"ROLE_ADMINISTRATOR", "ROLE_NEIGHBOR", "ROLE_WORKER"})
    public Response listWorkers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withProfessions") final List<String> professions,
            @QueryParam("inNeighborhoods") final List<String> neighborhoodIds,
            @QueryParam("withRole") final String workerRole,
            @QueryParam("withStatus") final String workerStatus
    ) {
        LOGGER.info("GET request arrived at '/workers'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        Set<Worker> workers = ws.getWorkers(page, size, professions, neighborhoodIds, workerRole, workerStatus);
        if (workers.isEmpty())
            return Response.noContent().build();
        List<WorkerDto> workerDto = workers.stream()
                .map(w -> WorkerDto.fromWorker(w, uriInfo)).collect(Collectors.toList());

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "workers",
                ws.calculateWorkerPages(professions, neighborhoodIds, size, workerRole, workerStatus),
                page,
                size
        );

        return Response.ok(new GenericEntity<List<WorkerDto>>(workerDto){})
                .links(links)
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findWorker(
            @PathParam("id") final long workerId
    ) {
        LOGGER.info("GET request arrived at '/workers/{}'", workerId);

        // Content
        Worker worker = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found"));

        // Cache Control
        EntityTag entityTag = new EntityTag(worker.getVersion().toString());
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerDto.fromWorker(worker, uriInfo))
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createWorker(
            @Valid final WorkerSignupForm form
    ) {
        LOGGER.info("POST request arrived at '/workers'");

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        // Creation & Etag Generation
        final Worker worker = ws.createWorker(form.getWorker_mail(), form.getWorker_name(), form.getWorker_surname(), form.getWorker_password(), form.getWorker_identification(), form.getPhoneNumber(), form.getAddress(), form.getWorker_languageURN(), form.getProfessionURNs(), form.getBusinessName());
        entityLevelETag = ETagUtility.generateETag();

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(worker.getWorkerId())).build();

        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateWorkerPartially(
            @PathParam("id") final long workerId,
            @Valid final WorkerUpdateForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/workers/{}'", workerId);

        // Check If-Match header
        if (ifMatch != null){
            String rowVersion = ws.findWorker(workerId).orElseThrow(() -> new NotFoundException("Worker Not Found")).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(rowVersion));
            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, rowVersion)
                        .build();
        }

        // Usual Flow
        final Worker worker = ws.updateWorkerPartially(workerId, partialUpdate.getPhoneNumber(), partialUpdate.getAddress(), partialUpdate.getBusinessName(), partialUpdate.getBackgroundPicture(), partialUpdate.getBio());
        entityLevelETag = ETagUtility.generateETag();
        return Response.ok(WorkerDto.fromWorker(worker, uriInfo))
                .header(HttpHeaders.ETAG, entityLevelETag)
                .build();
    }
}
