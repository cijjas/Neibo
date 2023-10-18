package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
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
    public Optional<Image> getImage(long imageId) {
        LOGGER.info("Retrieving Image {}", imageId);
        return imageDao.getImage(imageId);
    }
}
