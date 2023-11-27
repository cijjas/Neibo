package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageDao {

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    Image storeImage(MultipartFile image);

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    Optional<Image> getImage(long imageId);
}
