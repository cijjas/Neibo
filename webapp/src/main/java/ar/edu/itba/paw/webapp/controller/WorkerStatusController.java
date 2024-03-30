package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.WorkerStatus;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import ar.edu.itba.paw.webapp.dto.WorkerStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("worker-statuses")
@Component
public class WorkerStatusController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerStatusController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWorkerStatuses() {
        LOGGER.info("GET request arrived at '/worker-statuses'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<WorkerStatusDto> workerStatusDto = Arrays.stream(WorkerStatus.values())
                .map(tt -> WorkerStatusDto.fromWorkerStatus(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerStatusDto>>(workerStatusDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findWorkerStatus(
            @PathParam("id") final int id
    ) {
        LOGGER.info("GET request arrived at '/worker-statuses/{}'", id);
        return Response.ok(WorkerStatusDto.fromWorkerStatus(WorkerStatus.fromId(id), uriInfo)).build();
    }
}