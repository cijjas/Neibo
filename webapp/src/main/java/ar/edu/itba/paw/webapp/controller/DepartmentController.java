package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.DepartmentService;
import ar.edu.itba.paw.models.Entities.Department;
import ar.edu.itba.paw.webapp.dto.DepartmentDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;



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
@Produces(value = {MediaType.APPLICATION_JSON,})
public class DepartmentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService ds;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Autowired
    public DepartmentController(DepartmentService ds) {
        this.ds = ds;
    }

    @GET
    public Response listDepartments() {
        LOGGER.info("GET request arrived at '/departments'");

        // Content
        List<Department> departments = ds.getDepartments();
        String departmentsHashCode = String.valueOf(departments.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(departmentsHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (departments.isEmpty())
            return Response.noContent()
                    .tag(departmentsHashCode)
                    .build();

        List<DepartmentDto> departmentDto = departments.stream()
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo)).collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto) {
                })
                .cacheControl(cacheControl)
                .tag(departmentsHashCode)
                .build();
    }

    @GET
    @Path("/{departmentId}")
    public Response findDepartment(
            @PathParam("departmentId") @GenericIdConstraint Long departmentId
    ) {
        LOGGER.info("GET request arrived at '/departments/{}'", departmentId);

        // Content
        Department department = ds.findDepartment(departmentId).orElseThrow(NotFoundException::new);
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

    @POST
    @Secured({"ROLE_SUPER_ADMINISTRATOR"})
    @Validated(CreateValidationSequence.class)
    public Response createDepartment(
            @Valid DepartmentDto createForm
    ) {
        LOGGER.info("POST request arrived at '/departments'");

        // Content
        final Department department = ds.createDepartment(createForm.getName());
        String departmentHashCode = String.valueOf(department.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(department.getDepartmentId())).build();

        // Cache Control
        CacheControl cacheControl = new CacheControl();

        return Response.created(uri)
                .cacheControl(cacheControl)
                .tag(departmentHashCode)
                .build();
    }

    @DELETE
    @Path("/{departmentId}")
    @Secured({"ROLE_SUPER_ADMINISTRATOR"})
    public Response deleteDepartment(
            @PathParam("departmentId") @GenericIdConstraint long departmentId
    ) {
        LOGGER.info("DELETE request arrived at '/departments/{}'", departmentId);

        // Deletion Attempt
        if (ds.deleteDepartment(departmentId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
