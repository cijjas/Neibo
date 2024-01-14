package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UserUpdateForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PathParam("neighborhoodId")
    private Long neighborhoodId;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("size") @DefaultValue("10") final int size,
            @QueryParam("userRole") final UserRole userRole
    ) {
        LOGGER.info("GET request arrived at neighborhoods/{}/users", neighborhoodId);
        final List<User> users = us.getUsers(userRole, neighborhoodId, page, size);
        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/users";
        int totalAmenityPages = us.calculateUserPages(userRole, neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);

        return Response.ok(new GenericEntity<List<UserDto>>(usersDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findUser(@PathParam("id") final long id) {
        LOGGER.info("GET request arrived at neighborhoods/{}/users/{}", neighborhoodId, id);
        return Response.ok(UserDto.fromUser(us.findUser(id)
                .orElseThrow(() -> new NotFoundException("User Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid final SignupForm form) {
        LOGGER.info("POST request arrived at neighborhoods/{}/users", neighborhoodId);
        final User user = us.createNeighbor(form.getMail(), form.getPassword(), form.getName(), form.getSurname(), neighborhoodId, Language.ENGLISH, form.getIdentification());
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.getUserId())).build();
        return Response.created(uri).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response updateUserPartially(
            @PathParam("id") final long id,
            @Valid final UserUpdateForm partialUpdate) {
        LOGGER.info("PATCH request arrived at neighborhoods/{}/users/{}", neighborhoodId, id);
        final User user = us.updateUser(id, partialUpdate.getEmail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePicture(), partialUpdate.getIdentification(), partialUpdate.getLanguageId(), partialUpdate.getUserRoleId());
        return Response.ok(UserDto.fromUser(user, uriInfo)).build();
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