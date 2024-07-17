package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.dto.WorkerStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

/*
 * # Summary
 *   - A Worker Criteria, a Worker can be HOT if he received many positive reviews in the last couple of days
 *
 * # Use cases
 *   - A User/Admin/Worker can filter the workers through this criteria
 */

@Path("worker-statuses")
@Component
public class WorkerStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWorkerStatuses() {
        LOGGER.info("GET request arrived at '/worker-statuses'");

        // Content
        WorkerStatus[] workerStatuses = WorkerStatus.values();
        String workerStatusesHashCode = String.valueOf(Arrays.hashCode(workerStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerStatusesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<WorkerStatusDto> workerStatusDto = Arrays.stream(workerStatuses)
                .map(tt -> WorkerStatusDto.fromWorkerStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerStatusDto>>(workerStatusDto){})
                .cacheControl(cacheControl)
                .tag(workerStatusesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findWorkerStatus(
            @PathParam("id") final int id
    ) {
        LOGGER.info("GET request arrived at '/worker-statuses/{}'", id);

        // Content
        WorkerStatus workerStatus = WorkerStatus.fromId(id);
        String workerStatusHashCode = String.valueOf(workerStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerStatusDto.fromWorkerStatus(workerStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(workerStatusHashCode)
                .build();
    }
}