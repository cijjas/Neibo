package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Pair;
import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.ChannelDto;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.dto.NeighborhoodDto;
import org.omg.CosNaming.NamingContextPackage.NotFound;
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

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDepartments() {
        List<DepartmentDto> departmentDto = Arrays.stream(Department.values())
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto){}).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findDepartment(@PathParam("id") final int id) {
        Department department = Department.fromId(id);

        if (department != null) {
            DepartmentDto departmentDto = DepartmentDto.fromDepartment(department, uriInfo);
            return Response.ok(departmentDto).build();
        } else {
            throw new NotFoundException("Department not found");
        }
    }

}
