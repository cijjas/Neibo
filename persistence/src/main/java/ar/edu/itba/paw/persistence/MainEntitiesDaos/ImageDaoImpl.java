package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.exceptions.UnexpectedException;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Entities.Image;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    @Override
    public Image storeImage(InputStream imageStream) {
        LOGGER.debug("Inserting Image");

        byte[] imageBytes;
        try {
            // Read the input stream into a byte array
            imageBytes = IOUtils.toByteArray(imageStream);
        } catch (IOException e) {
            throw new UnexpectedException("An error occurred while storing the image");
        }

        Image img = new Image.Builder()
                .image(imageBytes)
                .build();
        em.persist(img);
        return img;
    }


    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    @Override
    public Optional<Image> findImage(long imageId) {
        LOGGER.debug("Selecting Image with id {}", imageId);

        return Optional.ofNullable(em.find(Image.class, imageId));
    }

    @Override
    public boolean deleteImage(long imageId) {
        LOGGER.debug("Deleting Image with imageId {}", imageId);
        Image image = em.find(Image.class, imageId);
        if (image != null) {
            em.remove(image);
            return true;
        }
        return false;
    }
}
