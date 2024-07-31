package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Image;

import java.io.InputStream;
import java.util.Optional;

public interface ImageService {

    // -----------------------------------------------------------------------------------------------------------------

    Image storeImage(InputStream image);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Image> findImage(long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteImage(long imageId);
}
