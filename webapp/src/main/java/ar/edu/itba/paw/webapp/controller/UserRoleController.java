package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.constants.Constant.IMMUTABLE;
import static ar.edu.itba.paw.webapp.controller.constants.Constant.MAX_AGE_SECONDS;

/*
 * # Summary
 *   - Criteria for Users
 *
 * # Use cases
 *   - A Neighbor/Admin can filter the Users through this criteria
 */

@Path(Endpoint.API + "/" + Endpoint.USER_ROLES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class UserRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @GET
    public Response listUserRoles() {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        UserRole[] userRoles = UserRole.values();
        String userRoleHashCode = String.valueOf(Arrays.hashCode(userRoles));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(userRoleHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<UserRoleDto> userRoleDto = Arrays.stream(UserRole.values())
                .map(tt -> UserRoleDto.fromUserRole(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<UserRoleDto>>(userRoleDto) {
                })
                .cacheControl(cacheControl)
                .tag(userRoleHashCode)
                .build();
    }

    @GET
    @Path("{" + PathParameter.USER_ROLE_ID + "}")
    public Response findUserRole(
            @PathParam(PathParameter.USER_ROLE_ID) long userRoleId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        UserRole userRole = UserRole.fromId(userRoleId).orElseThrow(NotFoundException::new);
        String userRoleHashCode = String.valueOf(userRole.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        cacheControl.getCacheExtension().put(IMMUTABLE, "");
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(userRoleHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(UserRoleDto.fromUserRole(userRole, uriInfo))
                .cacheControl(cacheControl)
                .tag(userRoleHashCode)
                .build();
    }
}