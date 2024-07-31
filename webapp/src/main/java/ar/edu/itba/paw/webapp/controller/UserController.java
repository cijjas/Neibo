package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UserUpdateForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkModificationETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkMutableETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;

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

    private EntityTag entityLevelETag = ETagUtility.generateETag();

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON})
    @PreAuthorize("@accessControlHelper.hasAccessToUserList(#neighborhoodId)")
    public Response listUsers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("withRole") final String userRoleURN,
            @PathParam("neighborhoodId") final long neighborhoodId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Content
        final List<User> users = us.getUsers(userRoleURN, neighborhoodId, page, size);
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
                us.calculateUserPages(userRoleURN, neighborhoodId, size),
                page,
                size
        );

        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(usersDto){})
                .cacheControl(cacheControl)
                .tag(usersHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.hasAccessToUserDetail(#neighborhoodId, #id)")
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
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(
            @Valid @NotNull final SignupForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Creation & ETag Generation
        final User user = us.createNeighbor(form.getEmail(), form.getPassword(), form.getName(), form.getSurname(), neighborhoodId, form.getLanguageURN(), form.getIdentification());
        String userHashCode = String.valueOf(user.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getUserId())).build();

        return Response.created(uri)
                .tag(userHashCode)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canUpdateUser(#id, #neighborhoodId)")
    public Response updateUserPartially(
            @PathParam("id") final long id,
            @PathParam("neighborhoodId") final long neighborhoodId,
            @Valid @NotNull final UserUpdateForm partialUpdate
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);


        // Modification & HashCode Generation
        final User updatedUser = us.updateUser(id, partialUpdate.getEmail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePictureURN(), partialUpdate.getIdentification(), partialUpdate.getLanguageURN(), partialUpdate.getUserRoleURN());
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
        if(us.deleteUser(userId)) {
            return Response.noContent()
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }*/
}