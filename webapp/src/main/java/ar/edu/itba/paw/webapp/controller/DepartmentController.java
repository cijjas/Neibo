package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Pair;
import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.*;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("departments")
@Component
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDepartments() {
        LOGGER.info("GET request arrived at '/departments'");
        List<DepartmentDto> departmentDto = Arrays.stream(Department.values())
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findDepartment(@PathParam("id") final int id) {
        LOGGER.info("GET request arrived at '/departments/{}'", id);
        return Response.ok(DepartmentDto.fromDepartment(Department.fromId(id), uriInfo)).build();
    }

}
