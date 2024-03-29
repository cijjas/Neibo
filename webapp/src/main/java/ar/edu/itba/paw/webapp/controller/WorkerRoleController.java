package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import ar.edu.itba.paw.webapp.dto.WorkerRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("worker-roles")
@Component
public class WorkerRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRoleController.class);

    @Context
    private UriInfo uriInfo;

    private final String storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWorkerRoles(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                    @Context Request request) {
        LOGGER.info("GET request arrived at '/worker-roles'");
        List<WorkerRoleDto> workerRoleDto = Arrays.stream(WorkerRole.values())
                .map(tt -> WorkerRoleDto.fromWorkerRole(tt, uriInfo))
                .collect(Collectors.toList());

        EntityTag entityTag = new EntityTag(storedETag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");

        return Response.ok(new GenericEntity<List<WorkerRoleDto>>(workerRoleDto){})
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findWorkerRole(@PathParam("id") final int id,
                                   @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                   @Context Request request) {
        LOGGER.info("GET request arrived at '/worker-roles/{}'", id);
        WorkerRoleDto workerRoleDto = WorkerRoleDto.fromWorkerRole(WorkerRole.fromId(id), uriInfo);

        EntityTag entityTag = new EntityTag(storedETag);
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(workerRoleDto)
                .cacheControl(cacheControl)
                .tag(entityTag)
                .build();
    }
}