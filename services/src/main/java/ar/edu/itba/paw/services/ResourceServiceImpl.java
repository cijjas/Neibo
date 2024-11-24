package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
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

import java.util.List;
import java.util.Optional;

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
    public Resource createResource(long neighborhoodId, String title, String description, Long imageId) {
        LOGGER.info("Creating Resource {} for Neighborhood {}", title, neighborhoodId);

        return resourceDao.createResource(neighborhoodId, title, description, imageId == null ? 0 : imageId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public Optional<Resource> findResource(long resourceId) {
        LOGGER.info("Finding Resource {}", resourceId);

        return resourceDao.findResource(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResources(final long neighborhoodId) {
        LOGGER.info("Getting Resources from Neighborhood {}", neighborhoodId);

        return resourceDao.getResources(neighborhoodId);
    }

    @Override
    public Optional<Resource> findResource(long resourceId, long neighborhoodId) {
        LOGGER.info("Finding Resource {} from Neighborhood {}", resourceId, neighborhoodId);

        return resourceDao.findResource(resourceId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Resource updateResource(long resourceId, String title, String description, Long imageId) {
        LOGGER.info("Updating Resource {}", resourceId);

        Resource resource = findResource(resourceId).orElseThrow(NotFoundException::new);

        if (title != null && !title.isEmpty())
            resource.setTitle(title);
        if (description != null && !description.isEmpty())
            resource.setDescription(description);

        if (imageId != null) {
            Image i = imageService.findImage(imageId).orElseThrow(NotFoundException::new);
            resource.setImage(i);
        }

        return resource;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteResource(final long resourceId) {
        LOGGER.info("Deleting Resource {}", resourceId);

        return resourceDao.deleteResource(resourceId);
    }
}

