package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.dto.RoleDto;
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

@Path("roles")
@Component
public class RoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRoles() {
        LOGGER.info("Listing Roles");
        List<RoleDto> rolesDto = Arrays.stream(UserRole.values())
                .map(ur -> RoleDto.fromRole(ur, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<RoleDto>>(rolesDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRole(@PathParam("id") final long id) {
        LOGGER.info("Finding Role with id {}", id);
        UserRole role = UserRole.fromId((int) id);

        if (role != null) {
            RoleDto roleDto = RoleDto.fromRole(role, uriInfo);
            return Response.ok(roleDto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Role not found").build();
        }
    }
}
