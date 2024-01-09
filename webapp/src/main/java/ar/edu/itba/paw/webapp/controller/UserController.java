package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UserUpdateForm;
import ar.edu.itba.paw.webapp.form.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.controller.ControllerUtils.createPaginationLinks;

@Path("neighborhoods/{neighborhoodId}/users")
@Component
public class UserController {

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
        final List<User> users = us.getUsersByCriteria(userRole, neighborhoodId, page, size);
        final List<UserDto> usersDto = users.stream()
                .map(u -> UserDto.fromUser(u, uriInfo)).collect(Collectors.toList());

        String baseUri = uriInfo.getBaseUri().toString() + "neighborhood/" + neighborhoodId + "/users";
        int totalAmenityPages = us.getTotalPages(userRole, neighborhoodId, size);
        Link[] links = createPaginationLinks(baseUri, page, size, totalAmenityPages);

        return Response.ok(new GenericEntity<List<UserDto>>(usersDto){})
                .links(links)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findUser(@PathParam("id") final long id) {
        return Response.ok(UserDto.fromUser(us.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found")), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid final SignupForm form) {
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
        final User user = us.updateUser(id, partialUpdate.getEmail(), partialUpdate.getName(), partialUpdate.getSurname(), partialUpdate.getPassword(), partialUpdate.getDarkMode(), partialUpdate.getPhoneNumber(), partialUpdate.getProfilePicture(), partialUpdate.getIdentification());
        return Response.ok(UserDto.fromUser(user, uriInfo)).build();
    }

    /*
    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        us.deleteById(id);
        return Response.noContent().build();
    }*/
}