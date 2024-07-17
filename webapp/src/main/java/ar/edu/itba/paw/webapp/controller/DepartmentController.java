package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.Pair;
import ar.edu.itba.paw.enums.DayOfTheWeek;
import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.webapp.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;


/*
 * # Summary
 *   - Product Criteria, many Products belong to the same Department
 *
 * # Use cases
 *   - A User/Admin can filter the products according to their department
 *
 * # Embeddable?
 *   - Not embeddable as it is used as a filter
 */


@Path("departments")
@Component
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDepartments() {
        LOGGER.info("GET request arrived at '/departments'");

        // Content
        Department[] departments = Department.values();
        String departmentsHashCode = String.valueOf(Arrays.hashCode(departments));

        //Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(departmentsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        List<DepartmentDto> departmentDto = Arrays.stream(Department.values())
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto){})
                .cacheControl(cacheControl)
                .tag(departmentsHashCode)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findDepartment(
            @PathParam("id") final int departmentId
    ) {
        LOGGER.info("GET request arrived at '/departments/{}'", departmentId);

        // Content
        Department department = Department.fromId(departmentId);
        String departmentHashCode = String.valueOf(department.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(departmentHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(DepartmentDto.fromDepartment(department, uriInfo))
                .cacheControl(cacheControl)
                .tag(departmentHashCode)
                .build();
    }
}
