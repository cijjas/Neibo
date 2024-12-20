package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserRoleURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateValidationSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalFirstId;

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
@Produces(value = {MediaType.APPLICATION_JSON})
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final UserService us;

    @Autowired
    public UserController(UserService us) {
        this.us = us;
    }

    @GET
    @PreAuthorize("@pathAccessControlHelper.canListUsers(#neighborhoodId)")
    public Response listUsers(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @QueryParam("withRole") @UserRoleURNConstraint String userRole,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // ID Extraction
        Long userRoleId = extractOptionalFirstId(userRole);

        // Content
        final List<User> users = us.getUsers(neighborhoodId, userRoleId, page, size);
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
                us.calculateUserPages(neighborhoodId, userRoleId, size),
                page,
                size
        );

        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(usersDto) {
                })
                .cacheControl(cacheControl)
                .tag(usersHashCode)
                .links(links)
                .build();
    }

    @GET
    @Path("/{userId}")
    @PreAuthorize("@pathAccessControlHelper.canFindUser(#neighborhoodId, #userId)")
    public Response findUser(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("userId") @GenericIdConstraint long userId

    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, userId);

        // Content
        User user = us.findUser(neighborhoodId, userId).orElseThrow(NotFoundException::new);
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
    @Validated(CreateValidationSequence.class)
    public Response createUser(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @Valid UserDto createForm
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Creation & ETag Generation
        final User user = us.createUser(neighborhoodId, createForm.getMail(), createForm.getName(), createForm.getSurname(), createForm.getPassword(), createForm.getIdentification(), extractOptionalFirstId(createForm.getLanguage()));
        String userHashCode = String.valueOf(user.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getUserId())).build();

        return Response.created(uri)
                .tag(userHashCode)
                .build();
    }

    @PATCH
    @Path("/{userId}")
    @PreAuthorize("@pathAccessControlHelper.canUpdateUser(#userId, #neighborhoodId)")
    @Validated(UpdateValidationSequence.class)
    public Response updateUser(
            @PathParam("neighborhoodId") @NeighborhoodIdConstraint long neighborhoodId,
            @PathParam("userId") @GenericIdConstraint long userId,
            @Valid UserDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, userId);

        // Modification & HashCode Generation
        final User updatedUser = us.updateUser(neighborhoodId, userId, updateForm.getMail(), updateForm.getName(), updateForm.getSurname(), updateForm.getPassword(), updateForm.getIdentification(), extractOptionalFirstId(updateForm.getLanguage()), extractOptionalFirstId(updateForm.getProfilePicture()), updateForm.getDarkMode(), updateForm.getPhoneNumber(), extractOptionalFirstId(updateForm.getUserRole()));
        String updatedUserHashCode = String.valueOf(updatedUser.hashCode());

        return Response.ok(UserDto.fromUser(updatedUser, uriInfo))
                .tag(updatedUserHashCode)
                .build();
    }
}