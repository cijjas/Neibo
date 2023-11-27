package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    @Override
    public Image storeImage(MultipartFile image) {
        LOGGER.debug("Inserting Image {}", image.getName());
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (IOException e) {
            LOGGER.error("Error whilst getting the Image bytes", e);
            throw new InsertionException("An error occurred whilst storing the image");
        }
        Image img = new Image.Builder()
                .image(imageBytes)
                .build();
        em.persist(img);
        return img;
    }

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    @Override
    public Optional<Image> getImage(long imageId) {
        LOGGER.debug("Selecting Image with id {}", imageId);
        return Optional.ofNullable(em.find(Image.class, imageId));
    }
}
