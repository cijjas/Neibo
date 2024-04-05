package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UserUpdateForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkModificationETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.ETagUtility.checkMutableETagPreconditions;
import static ar.edu.itba.paw.webapp.controller.GlobalControllerAdvice.CUSTOM_ROW_LEVEL_ETAG_NAME;

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
            @QueryParam("withRole") final String userRole,
            @PathParam("neighborhoodId") final long neighborhoodId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        final List<User> users = us.getUsers(userRole, neighborhoodId, page, size);
        if (users.isEmpty())
            return Response.noContent().build();

        // Pagination Links
        Link[] links = createPaginationLinks(
                uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/users",
                us.calculateUserPages(userRole, neighborhoodId, size),
                page,
                size
        );

        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(usersDto){})
                .cacheControl(cacheControl)
                .tag(entityLevelETag)
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.hasAccessToUserDetail(#neighborhoodId, #id)")
    public Response findUser(
            @PathParam("id") final long id,
            @PathParam("neighborhoodId") final long neighborhoodId,
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) EntityTag clientETag

    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Content
        User user = us.findUser(id, neighborhoodId).orElseThrow(NotFoundException::new);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(user.getVersion().toString());
        Response response = checkMutableETagPreconditions(clientETag, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        return Response.ok(UserDto.fromUser(user, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(
            @Valid final SignupForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        // Cache Control
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .tag(entityLevelETag)
                    .build();

        // Creation & ETag Generation
        final User user = us.createNeighbor(form.getEmail(), form.getPassword(), form.getName(), form.getSurname(), neighborhoodId, form.getLanguageURN(), form.getIdentification());
        entityLevelETag = ETagUtility.generateETag();
        EntityTag rowLevelETag = new EntityTag(user.getVersion().toString());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getUserId())).build();

        return Response.created(uri)
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    @PreAuthorize("@accessControlHelper.canUpdateUser(#id, #neighborhoodId)")
    public Response updateUserPartially(
            @PathParam("id") final long id,
            @PathParam("neighborhoodId") final long neighborhoodId,
            @Valid final UserUpdateForm partialUpdate,
            @HeaderParam(HttpHeaders.IF_MATCH) EntityTag ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Cache Control
        EntityTag rowLevelETag = new EntityTag(us.findUser(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString());
        Response response = checkModificationETagPreconditions(ifMatch, entityLevelETag, rowLevelETag);
        if (response != null)
            return response;

        // Modification & ETag Generation
        final User updatedUser = us.updateUser(id, partialUpdate.getEmail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePictureURN(), partialUpdate.getIdentification(), partialUpdate.getLanguageURN(), partialUpdate.getUserRoleURN());
        entityLevelETag = ETagUtility.generateETag();
        rowLevelETag = new EntityTag(updatedUser.getVersion().toString());

        return Response.ok(UserDto.fromUser(updatedUser, uriInfo))
                .tag(entityLevelETag)
                .header(CUSTOM_ROW_LEVEL_ETAG_NAME, rowLevelETag)
                .build();
    }

    /*
    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        LOGGER.info("Deleting User with id {}", id);
        us.deleteById(id);
        return Response.noContent().build();
    }*/
}