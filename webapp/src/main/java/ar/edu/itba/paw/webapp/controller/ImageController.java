package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;


/*
 * # Summary
 *   - Self-Descriptive, has relationships with many entities like Posts, Products, Users, Workers, Resources
 *
 * # Use Cases
 *   - All Images are public and can be retrieved by any User
 */

@Path("images")
@Component
@Transactional
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ImageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    private final ImageService is;

    @Autowired
    public ImageController(ImageService is) {
        this.is = is;
    }

    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") @GenericIdConstraint long imageId
    ) {
        LOGGER.info("GET request arrived at '/images/{}'", imageId);

        // Content
        Image image = is.findImage(imageId).orElseThrow(NotFoundException::new);
        String imageHashCode = String.valueOf(image.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(imageHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        return Response.ok(ImageDto.fromImage(image, uriInfo))
                .cacheControl(cacheControl)
                .tag(imageHashCode)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeImage(
            @FormDataParam("imageFile") InputStream fileInputStream,
            @FormDataParam("imageFile") FormDataContentDisposition fileDetail
    ) {
        LOGGER.info("POST request arrived at '/images/'");

        // ??
        if (fileInputStream == null) {
            LOGGER.warn("Null Image InputStream");
            return Response.ok().build();
        }

        // Creation & HashCode Generation
        final Image image = is.storeImage(fileInputStream);
        String imageHashCode = String.valueOf(image.hashCode());

        // Resource URN
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(image.getImageId())).build();

        return Response.created(uri)
                .tag(imageHashCode)
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Secured("ROLE_SUPER_ADMINISTRATOR")
    public Response deleteById(
            @PathParam("id") @GenericIdConstraint final long imageId
    ) {
        LOGGER.info("DELETE request arrived at '/images/{}'", imageId);

        // Deletion Attempt
        if (is.deleteImage(imageId))
            return Response.noContent()
                    .build();

        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }
}
