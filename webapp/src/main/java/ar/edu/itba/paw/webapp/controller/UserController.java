package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.queryForms.UserParams;
import ar.edu.itba.paw.webapp.validation.groups.sequences.CreateSequence;
import ar.edu.itba.paw.webapp.validation.groups.sequences.UpdateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;
import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractOptionalFirstId;

/*
 * # Summary
 *   - Self descriptive
 *
 * # Use cases
 *   - Anyone can register as a User
 *   - A Registered User can update his profile
 *   - A Neighbor/Admin can list the Users in his Neighborhood
 *   - An Admin can update the profiles of the Neighbors it moderates
 */

@Path(Endpoint.API + "/" +  Endpoint.USERS)
@Component
@Validated
@Produces(value = {MediaType.APPLICATION_JSON})
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService us;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public UserController(UserService us) {
        this.us = us;
    }

    @GET
    @PreAuthorize("@accessControlHelper.canListUsers(#userParams.neighborhood, #userParams.userRole)")
    public Response listUsers(
            @Valid @BeanParam UserParams userParams
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // ID Extraction
        Long neighborhoodId = extractOptionalFirstId(userParams.getNeighborhood());
        Long userRoleId = extractOptionalFirstId(userParams.getUserRole());

        // Content
        final List<User> users = us.getUsers(neighborhoodId, userRoleId, userParams.getPage(), userParams.getSize());
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
                uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS),
                us.calculateUserPages(neighborhoodId, userRoleId, userParams.getSize()),
                userParams.getPage(),
                userParams.getSize()
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
    @Path("{" + PathParameter.USER_ID + "}")
    @PreAuthorize("@accessControlHelper.canFindUser(#userId)")
    public Response findUser(
            @PathParam(PathParameter.USER_ID) long userId
    ) {
        LOGGER.info("GET request arrived at '{}'", uriInfo.getRequestUri());

        // Content
        User user = us.findUser(userId).orElseThrow(NotFoundException::new);
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
    @Validated(CreateSequence.class)
    @PreAuthorize("@accessControlHelper.canCreateUser(#createForm.neighborhood, #createForm.userRole, #createForm.language)")
    public Response createUser(
            @Valid @NotNull UserDto createForm
    ) {
        LOGGER.info("POST request arrived at '{}'", uriInfo.getRequestUri());

        // Creation & ETag Generation
        final User user = us.createUser(extractFirstId(createForm.getNeighborhood()), createForm.getMail(), createForm.getName(), createForm.getSurname(), createForm.getPassword(), createForm.getIdentification(), extractOptionalFirstId(createForm.getLanguage()), extractOptionalFirstId(createForm.getUserRole()));
        String userHashCode = String.valueOf(user.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getUserId())).build();

        return Response.created(uri)
                .tag(userHashCode)
                .build();
    }

    @PATCH
    @Path("{" + PathParameter.USER_ID + "}")
    @Validated(UpdateSequence.class)
     @PreAuthorize("@accessControlHelper.canUpdateUser(#updateForm.neighborhood, #updateForm.userRole, #updateForm.language, #updateForm.profilePicture, #userId)")
    public Response updateUser(
            @PathParam(PathParameter.USER_ID) long userId,
            @Valid @NotNull UserDto updateForm
    ) {
        LOGGER.info("PATCH request arrived at '{}'", uriInfo.getRequestUri());

        // Modification & HashCode Generation
        final User updatedUser = us.updateUser(userId, extractOptionalFirstId(updateForm.getNeighborhood()), updateForm.getMail(), updateForm.getName(), updateForm.getSurname(), updateForm.getPassword(), updateForm.getIdentification(), extractOptionalFirstId(updateForm.getLanguage()), extractOptionalFirstId(updateForm.getProfilePicture()), updateForm.getDarkMode(), updateForm.getPhoneNumber(), extractOptionalFirstId(updateForm.getUserRole()));
        String updatedUserHashCode = String.valueOf(updatedUser.hashCode());

        return Response.ok(UserDto.fromUser(updatedUser, uriInfo))
                .tag(updatedUserHashCode)
                .build();
    }
}