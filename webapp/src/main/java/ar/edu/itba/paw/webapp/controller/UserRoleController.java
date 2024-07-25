package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.StandardTime;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.dto.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
        import javax.ws.rs.core.*;
        import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.*;

@Path("user-roles")
@Component
public class UserRoleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUserRoles() {
        LOGGER.info("GET request arrived at '/user-roles'");

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(MAX_AGE_SECONDS);
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        List<UserRoleDto> userRoleDto = Arrays.stream(UserRole.values())
                .map(tt -> UserRoleDto.fromUserRole(tt, uriInfo))
                .collect(Collectors.toList());

        return Response.ok(new GenericEntity<List<UserRoleDto>>(userRoleDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findUserRole(
            @PathParam("id") final int id,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag
    ) {
        LOGGER.info("GET request arrived at '/user-roles/{}'", id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(Long.toString(id));
        Response response = checkETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Content
        UserRoleDto userRoleDto = UserRoleDto.fromUserRole(UserRole.fromId(id), uriInfo);

        return Response.ok(userRoleDto)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .header(HttpHeaders.CACHE_CONTROL, MAX_AGE_HEADER)
                .build();
    }
}