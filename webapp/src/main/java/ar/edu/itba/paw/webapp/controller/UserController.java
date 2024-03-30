package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.dto.UserWorkerDto;
import ar.edu.itba.paw.webapp.form.UserUpdateForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

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

        // Alt-Content? Should be in Service
        if(neighborhoodId == 0) {
            final List<UserWorkerDto> usersDto = users.stream()
                    .map(u -> UserWorkerDto.fromUserWorker(u, uriInfo)).collect(Collectors.toList());
            return Response.ok(new GenericEntity<List<UserWorkerDto>>(usersDto){})
                    .links(links)
                    .build();
        }
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
            @PathParam("neighborhoodId") final long neighborhoodId
    ) {
        LOGGER.info("GET request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Content
        User user = us.findUser(id, neighborhoodId).orElseThrow(() -> new NotFoundException("User Not Found"));

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        EntityTag entityTag = new EntityTag(user.getVersion().toString());
        Response.ResponseBuilder builder = request.evaluatePreconditions(entityTag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        if(neighborhoodId != 0 ){
            return Response.ok(UserDto.fromUser(user, uriInfo))
                    .cacheControl(cacheControl)
                    .tag(entityTag)
                    .build();
        } else {
            return Response.ok(UserWorkerDto.fromUserWorker(user, uriInfo))
                    .cacheControl(cacheControl)
                    .tag(entityTag)
                    .build();
        }
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(
            @Valid final SignupForm form
    ) {
        LOGGER.info("POST request arrived at '/neighborhoods/{}/users'", neighborhoodId);

        Response.ResponseBuilder builder = request.evaluatePreconditions(entityLevelETag);
        if (builder != null)
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    .entity("Your cached version of the resource is outdated.")
                    .header(HttpHeaders.ETAG, entityLevelETag)
                    .build();

        final User user = us.createNeighbor(form.getMail(), form.getPassword(), form.getName(), form.getSurname(), neighborhoodId, Language.ENGLISH, form.getIdentification());
        entityLevelETag = ETagUtility.generateETag();
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getUserId())).build();
        return Response.created(uri)
                .header(HttpHeaders.ETAG, entityLevelETag)
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
            @HeaderParam(HttpHeaders.IF_MATCH) String ifMatch
    ) {
        LOGGER.info("PATCH request arrived at '/neighborhoods/{}/users/{}'", neighborhoodId, id);

        // Check If-Match header
        if (ifMatch != null) {
            String version = us.findUser(id, neighborhoodId).orElseThrow(NotFoundException::new).getVersion().toString();
            Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(version));

            if (builder != null)
                return Response.status(Response.Status.PRECONDITION_FAILED)
                        .header(HttpHeaders.ETAG, version)
                        .build();
        }

        final User user = us.updateUser(id, partialUpdate.getEmail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePicture(), partialUpdate.getIdentification(), partialUpdate.getLanguageURN(), partialUpdate.getUserRoleURN());
        entityLevelETag = ETagUtility.generateETag();
        return Response.ok(UserDto.fromUser(user, uriInfo))
                .header(HttpHeaders.ETAG, entityLevelETag)
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