package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {

    // -----------------------------------------------------------------------------------------------------------------

    Image storeImage(MultipartFile image);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Image> getImage(long imageId);
}
