package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.TimeDto;
import ar.edu.itba.paw.webapp.dto.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("user-roles")
@Component
public class UserRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUserRoles() {
        LOGGER.info("GET request arrived at '/user-roles'");
        List<UserRoleDto> userRoleDto = Arrays.stream(UserRole.values())
                .map(tt -> UserRoleDto.fromUserRole(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<UserRoleDto>>(userRoleDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findUserRole(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/user-roles/{}'", id);
        return Response.ok(UserRoleDto.fromUserRole(UserRole.fromId(id), uriInfo)).build();
    }
}