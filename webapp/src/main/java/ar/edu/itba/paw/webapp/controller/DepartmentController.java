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

    @Context
    private Request request;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDepartments() {
        LOGGER.info("GET request arrived at '/departments'");

        //Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            return builder.cacheControl(cacheControl).build();
        }

        // Content
        List<DepartmentDto> departmentDto = Arrays.stream(Department.values())
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findDepartment(
            @PathParam("id") final int id
    ) {
        LOGGER.info("GET request arrived at '/departments/{}'", id);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        DepartmentDto departmentDto = DepartmentDto.fromDepartment(Department.fromId(id), uriInfo);

        return Response.ok(departmentDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }
}
