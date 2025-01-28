package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.UserRole;
import ar.edu.itba.paw.webapp.validation.constraints.specific.GenericIdConstraint;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

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

@Path(Endpoint.IMAGES)
@Component
@Produces(value = {MediaType.APPLICATION_JSON,})
public class ImageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);
    private final ImageService is;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    @Autowired
    public ImageController(ImageService is) {
        this.is = is;
    }

    @GET
    @Path("{" + PathParameter.IMAGE_ID + "}")
    public Response findImage(
            @PathParam(PathParameter.IMAGE_ID) @GenericIdConstraint long imageId
    ) {
        LOGGER.info("GET request arrived at '/images/{}'", imageId);

        // Retrieve Image
        Image image = is.findImage(imageId).orElseThrow(NotFoundException::new);
        String imageHashCode = String.valueOf(image.hashCode());

        // Cache Control
        CacheControl cacheControl = new CacheControl();
        Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag(imageHashCode));
        if (builder != null)
            return builder.cacheControl(cacheControl).build();

        // Return image bytes directly
        return Response.ok((StreamingOutput) output -> output.write((image.getImage())), MediaType.APPLICATION_OCTET_STREAM)
                .cacheControl(cacheControl)
                .tag(imageHashCode)
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Secured({UserRole.REJECTED, UserRole.UNVERIFIED, UserRole.WORKER, UserRole.NEIGHBOR, UserRole.ADMINISTRATOR, UserRole.SUPER_ADMINISTRATOR})
    public Response storeImage(
            @FormDataParam("imageFile") InputStream fileInputStream,
            @FormDataParam("imageFile") FormDataContentDisposition fileDetail
    ) {
        LOGGER.info("POST request arrived at '/images/'");

        if (fileInputStream == null) {
            LOGGER.warn("Null Image InputStream");
            return Response.ok().build();
        }

        // Creation & HashCode Generation
        final Image image = is.storeImage(fileInputStream);
        String imageHashCode = String.valueOf(image.hashCode());

        // Resource URI
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(image.getImageId())).build();

        return Response.created(uri)
                .tag(imageHashCode)
                .build();
    }

    @DELETE
    @Path("{" + PathParameter.IMAGE_ID + "}")
    @Secured(UserRole.SUPER_ADMINISTRATOR)
    public Response deleteImage(
            @PathParam(PathParameter.IMAGE_ID) @GenericIdConstraint long imageId
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
