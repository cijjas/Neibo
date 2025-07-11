package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.WorkerStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - A Worker Criteria, a Worker can be HOT if he received many positive reviews in the last couple of days
 *
 * # Use cases
 *   - A Neighbor/Admin/Worker can filter the workers through this criteria
 */

@Path(Endpoint.API + "/" + Endpoint.WORKER_STATUSES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON})
public class WorkerStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listWorkerStatuses() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        WorkerStatus[] workerStatuses = WorkerStatus.values();
        String workerStatusesHashCode = String.valueOf(Arrays.hashCode(workerStatuses));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerStatusesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<WorkerStatusDto> workerStatusDto = Arrays.stream(workerStatuses)
                .map(tt -> WorkerStatusDto.fromWorkerStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerStatusDto>>(workerStatusDto) {
                })
                .cacheControl(cacheControl)
                .tag(workerStatusesHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.WORKER_STATUS_ID + "}")
    public Response findWorkerStatus(
            @PathParam(PathParameter.WORKER_STATUS_ID) long workerStatusId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        WorkerStatus workerStatus = WorkerStatus.fromId(workerStatusId).orElseThrow(NotFoundException::new);
        String workerStatusHashCode = String.valueOf(workerStatus.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerStatusHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerStatusDto.fromWorkerStatus(workerStatus, uriInfo))
                .cacheControl(cacheControl)
                .tag(workerStatusHashCode)
                .build();
    }
}