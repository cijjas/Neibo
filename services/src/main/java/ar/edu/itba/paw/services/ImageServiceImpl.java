package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Image storeImage(InputStream imageStream) {
        LOGGER.info("Storing Image");

        return imageDao.storeImage(imageStream);
    }


    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Image> findImage(long imageId) {
        LOGGER.info("Finding Image {}", imageId);

        return imageDao.findImage(imageId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteImage(long imageId) {
        LOGGER.info("Deleting Neighborhood {}", imageId);

        return imageDao.deleteImage(imageId);
    }
}
