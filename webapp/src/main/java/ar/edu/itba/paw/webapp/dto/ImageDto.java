package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Image;

import javax.ws.rs.core.UriInfo;

public class ImageDto {

    private byte[] image;

    private Links _links;

    public static ImageDto fromImage(Image image, UriInfo uriInfo) {
        final ImageDto dto = new ImageDto();

        dto.image = image.getImage();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(image.getImageId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
