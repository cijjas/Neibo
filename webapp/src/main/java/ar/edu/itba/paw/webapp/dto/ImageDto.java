package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Amenity;
import ar.edu.itba.paw.models.Entities.Image;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class ImageDto {

    private byte[] image;
    private URI self;


    public static ImageDto fromImage(Image image, UriInfo uriInfo){
        final ImageDto dto = new ImageDto();

        dto.image = image.getImage();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("images")
                .path(String.valueOf(image.getImageId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
