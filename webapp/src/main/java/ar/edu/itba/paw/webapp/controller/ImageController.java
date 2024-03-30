package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Path("images")
@Component
public class ImageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService is;

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final EntityTag storedETag = ETagUtility.generateETag();

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response findById(
            @PathParam("id") long id
    ) {
        LOGGER.info("GET request arrived at '/images/{}'", id);

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(storedETag);
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Content
        ImageDto imageDto = ImageDto.fromImage(is.findImage(id).orElseThrow(NotFoundException::new), uriInfo);

        return Response.ok(imageDto)
                .cacheControl(cacheControl)
                .tag(storedETag)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeImage(
            @FormDataParam("imageFile") InputStream fileInputStream,
            @FormDataParam("imageFile") FormDataContentDisposition fileDetail
    ) {
        LOGGER.info("POST request arrived at '/images/'");

        if (fileInputStream == null) {
            LOGGER.warn("Null Image InputStream");
            return Response.ok().build();
        }

        // Save the image using your service
        final Image image = is.storeImage(fileInputStream);

        // Build URI for the newly created resource
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(image.getImageId())).build();

        // Return response with created status and URI of the new resource
        return Response.created(uri).build();
    }

}
