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

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDepartments(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                    @Context Request request) {
        LOGGER.info("GET request arrived at '/departments'");
        List<DepartmentDto> departmentDto = Arrays.stream(Department.values())
                .map(d -> DepartmentDto.fromDepartment(d, uriInfo))
                .collect(Collectors.toList());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");

        return Response.ok(new GenericEntity<List<DepartmentDto>>(departmentDto){})
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findDepartment(@PathParam("id") final int id,
                                   @HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                                   @Context Request request) {
        LOGGER.info("GET request arrived at '/departments/{}'", id);

        DepartmentDto departmentDto = DepartmentDto.fromDepartment(Department.fromId(id), uriInfo);

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);

        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null) {
            LOGGER.info("Cached");
            return builder.cacheControl(cacheControl).build();
        }

        LOGGER.info("New");
        return Response.ok(departmentDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

}
