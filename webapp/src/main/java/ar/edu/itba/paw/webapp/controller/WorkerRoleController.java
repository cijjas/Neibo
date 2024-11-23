package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.webapp.dto.WorkerRoleDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.MAX_AGE_SECONDS;

@Path("worker-roles")
@Component
public class WorkerRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRoleController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWorkerRoles() {
        LOGGER.info("GET request arrived at '/worker-roles'");

        // Content
        WorkerRole[] workerRoles = WorkerRole.values();
        String workerRolesHashCode = String.valueOf(Arrays.hashCode(workerRoles));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerRolesHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<WorkerRoleDto> workerRoleDto = Arrays.stream(WorkerRole.values())
                .map(tt -> WorkerRoleDto.fromWorkerRole(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerRoleDto>>(workerRoleDto) {
                })
                .cacheControl(cacheControl)
                .tag(workerRolesHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response findWorkerRole(
            @PathParam("id") @GenericIdConstraint final Long id
    ) {
        LOGGER.info("GET request arrived at '/worker-roles/{}'", id);

        // Content
        WorkerRole workerRole = WorkerRole.fromId(id);
        String workerRoleHashCode = String.valueOf(workerRole.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerRoleHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerRoleDto.fromWorkerRole(WorkerRole.fromId(id), uriInfo))
                .cacheControl(cacheControl)
                .tag(workerRoleHashCode)
                .build();
    }
}