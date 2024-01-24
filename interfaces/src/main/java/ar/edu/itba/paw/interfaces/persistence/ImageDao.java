package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

public interface ImageDao {

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    Image storeImage(MultipartFile image);

    Image storeImage(InputStream image);

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    Optional<Image> findImage(long imageId);
}
