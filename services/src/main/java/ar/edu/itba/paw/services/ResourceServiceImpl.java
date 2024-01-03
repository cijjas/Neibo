package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ResourceDao;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.ResourceService;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final ResourceDao resourceDao;
    private final ImageService imageService;

    @Autowired
    public ResourceServiceImpl(final ResourceDao resourceDao, ImageService imageService) {
        this.resourceDao = resourceDao;
        this.imageService = imageService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Resource createResource(long neighborhoodId, String title, String description, MultipartFile imageFile) {
        LOGGER.info("Creating Resource {} for Neighborhood {}", title, neighborhoodId);
        Image i = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            i = imageService.storeImage(imageFile);
        }
        return resourceDao.createResource(neighborhoodId, title, description, i == null ? 0 : i.getImageId());
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResources(final long neighborhoodId) {
        LOGGER.info("Getting Resources from Neighborhood {}", neighborhoodId);
        return resourceDao.getResources(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteResource(final long resourceId) {
        LOGGER.info("Deleting Resource {}", resourceId);
        return resourceDao.deleteResource(resourceId);
    }

    public Resource getResource(long id) {
        LOGGER.info("Getting Resource {}", id);
        return resourceDao.findResourceById(id).orElseThrow(() -> new IllegalStateException("Resource not found"));
    }

    @Override
    public Resource updateResource(long id, String title, String description, MultipartFile image) {
        LOGGER.info("Updating Resource {}", id);

        Resource resource = getResource(id);

        if(title != null && !title.isEmpty())
            resource.setTitle(title);
        if(description != null && !description.isEmpty())
            resource.setDescription(description);
        Image i = null;
        if (image != null && !image.isEmpty()) {
            i = imageService.storeImage(image);
            resource.setImage(i);
        }

        return resource;
    }
}

