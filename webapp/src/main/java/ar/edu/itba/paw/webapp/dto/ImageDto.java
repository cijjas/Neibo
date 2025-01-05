package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.webapp.validation.constraints.specific.ImageConstraint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ImageDto {

    @ImageConstraint
    private byte[] data;

    private Links _links;

    public static ImageDto fromImage(Image image, UriInfo uriInfo) {
        final ImageDto dto = new ImageDto();

        dto.data = image.getImage();

        Links links = new Links();

        String imageId = String.valueOf(image.getImageId());

        UriBuilder imageUri = uriInfo.getBaseUriBuilder().path(Endpoint.IMAGES.toString()).path(imageId);

        links.setSelf(imageUri.build());

        dto.set_links(links);
        return dto;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
