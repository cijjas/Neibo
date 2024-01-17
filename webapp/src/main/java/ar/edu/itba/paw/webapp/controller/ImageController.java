package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;

@Path("images")
@Component
public class ImageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService is;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response findById(@PathParam("id") long id) {
        LOGGER.info("GET request arrived at images/{}", id);
        Optional<Image> image = is.findImage(id);
        if (!image.isPresent()) {
            throw new NotFoundException("Image not found");
        }
        return Response.ok(ImageDto.fromImage(image.get(), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response storeImage(MultipartFile imageFile) {
        LOGGER.info("POST request arrived at images/");
        final Image image = is.storeImage(imageFile);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(image.getImageId())).build();
        return Response.created(uri).build();
    }
}

