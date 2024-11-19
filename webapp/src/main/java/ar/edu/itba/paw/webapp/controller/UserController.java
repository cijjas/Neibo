package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

/*
 * # Summary
 *   - Self descriptive
 *
 * # Use cases
 *   - A Client can register as a User
 *   - A User can update his profile
 *   - An Admin can list the Users in his Neighborhood
 *   - A User can see the profile of a particular User
 */

@Path("neighborhoods/{neighborhoodId}/users")
@Component
@Validated
@Transactional
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @PreAuthorize("@accessControlHelper.canListUsers(#neighborhoodId)")
    public Response listUsers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withRole") final String userRole,
            @PathParam("neighborhoodId") final long neighborhoodId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Content
        final List<User> users = us.getUsers(userRole, neighborhoodId, page, size);
        String usersHashCode = String.valueOf(users.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(usersHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if (users.isEmpty())
            return Response.noContent()
                    .tag(usersHashCode)
                    .build();

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/users",
                us.calculateUserPages(userRole, neighborhoodId, size),
                page,
                size
        );

        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(usersDto) {})
                .cacheControl(cacheControl)
                .tag(usersHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canFindUser(#neighborhoodId, #id)")
    public Response findUser(
            @PathParam("id") final long id,
            @PathParam("neighborhoodId") final long neighborhoodId

    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Content
        User user = us.findUser(id, neighborhoodId).orElseThrow(() -> new NotFoundException("User not found"));
        String userHashCode = String.valueOf(user.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(userHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(UserDto.fromUser(user, uriInfo))
                .cacheControl(cacheControl)
                .tag(userHashCode)
                .build();
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @Validated(OnCreate.class)
    public Response createUser(
            @Valid UserDto form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Creation & ETag Generation
        final User user = us.createNeighbor(form.getMail(), form.getPassword(), form.getName(), form.getSurname(), neighborhoodId, form.getLanguage(), form.getIdentification());
        String userHashCode = String.valueOf(user.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getUserId())).build();

        return Response.created(uri)
                .tag(userHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    @PreAuthorize("@accessControlHelper.canUpdateUser(#id, #neighborhoodId)")
    @Validated(OnUpdate.class)
    public Response updateUserPartially(
            @PathParam("id") final long id,
            @PathParam("neighborhoodId") final long neighborhoodId,
            @Valid UserDto partialUpdate
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Modification & HashCode Generation
        final User updatedUser = us.updateUser(id, partialUpdate.getMail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePicture(), partialUpdate.getIdentification(), partialUpdate.getLanguage(), partialUpdate.getUserRole());
        String updatedUserHashCode = String.valueOf(updatedUser.hashCode());

        return Response.ok(UserDto.fromUser(updatedUser, uriInfo))
                .tag(updatedUserHashCode)
                .build();
    }

/*    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteById(@PathParam("id") final long userId) {
        LOGGER.info("DELETE request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, userId);

        // Deletion Attempt
        if(us.deleteUser(userId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }*/
}