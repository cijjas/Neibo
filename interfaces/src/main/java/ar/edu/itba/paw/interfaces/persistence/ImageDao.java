package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Image;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface    ImageDao {

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    Image storeImage(MultipartFile image);

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    Optional<Image> getImage(long imageId);
}
