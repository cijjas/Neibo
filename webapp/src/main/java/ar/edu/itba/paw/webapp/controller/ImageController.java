package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Post;
import ar.edu.itba.paw.webapp.dto.AmenityDto;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import ar.edu.itba.paw.webapp.form.PublishForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
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
            throw new NotFoundException("Image not found");
        }
        return Response.ok(ImageDto.fromImage(image.get(), uriInfo)).build();
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response storeImage(MultipartFile imageFile) {
        final Image image = is.storeImage(imageFile);
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(image.getImageId())).build();
        return Response.created(uri).build();
    }
}

