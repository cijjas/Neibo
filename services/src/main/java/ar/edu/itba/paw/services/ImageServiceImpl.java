package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Entities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public Image storeImage(MultipartFile image) {
        LOGGER.info("Storing Image {}", image.getName());

        return imageDao.storeImage(image);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Image> findImage(long imageId) {
        LOGGER.info("Retrieving Image {}", imageId);

        ValidationUtils.checkImageId(imageId);

        if (imageId <= 0)
            throw new IllegalArgumentException("Image ID must be a positive integer");
        return imageDao.findImage(imageId);
    }
}
