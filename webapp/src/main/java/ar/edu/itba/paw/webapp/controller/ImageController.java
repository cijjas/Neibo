package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("images")
@Component
public class ImageController {

    @Autowired
    private ImageService is;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findById(@QueryParam("id") final long id) {
        Optional<Image> image = is.getImage(id);
        if (!image.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(ImageDto.fromImage(image.get(), uriInfo)).build();
    }

    //solo falta un post para storeImage(MultipartFile image)
}

