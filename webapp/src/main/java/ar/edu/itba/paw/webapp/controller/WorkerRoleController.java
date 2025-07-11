package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.WorkerRole;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.WorkerRoleDto;
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
 *   - Criteria for Workers
 *
 * # Use cases
 *   - A Worker/Neighbor/Admin can filter the Workers through this criteria
 */

@Path(Endpoint.API + "/" + Endpoint.WORKER_ROLES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class WorkerRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRoleController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listWorkerRoles() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        WorkerRole[] workerRoles = WorkerRole.values();
        String workerRolesHashCode = String.valueOf(Arrays.hashCode(workerRoles));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
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
    @Path("{" + PathParameter.WORKER_ROLE_ID + "}")
    public Response findWorkerRole(
            @PathParam(PathParameter.WORKER_ROLE_ID) long workerRoleId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        WorkerRole workerRole = WorkerRole.fromId(workerRoleId).orElseThrow(NotFoundException::new);
        String workerRoleHashCode = String.valueOf(workerRole.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(workerRoleHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(WorkerRoleDto.fromWorkerRole(workerRole, uriInfo))
                .cacheControl(cacheControl)
                .tag(workerRoleHashCode)
                .build();
    }
}