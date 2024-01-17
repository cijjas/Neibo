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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listWorkerRoles() {
        LOGGER.info("GET request arrived at '/worker-roles'");
        List<WorkerRoleDto> workerRoleDto = Arrays.stream(WorkerRole.values())
                .map(tt -> WorkerRoleDto.fromWorkerRole(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<WorkerRoleDto>>(workerRoleDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findWorkerRole(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/worker-roles/{}'", id);
        return Response.ok(WorkerRoleDto.fromWorkerRole(WorkerRole.fromId(id), uriInfo)).build();
    }
}