package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.dto.RoleDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("roles")
@Component
public class RoleController {

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRoles() {
        List<RoleDto> rolesDto = Arrays.stream(UserRole.values())
                .map(ur -> RoleDto.fromRole(ur, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<RoleDto>>(rolesDto){}).build();
    }
}
