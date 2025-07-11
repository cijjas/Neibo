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
    public ResourceServiceImpl(ResourceDao resourceDao, ImageService imageService) {
        this.resourceDao = resourceDao;
        this.imageService = imageService;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Resource createResource(long neighborhoodId, String title, String description, Long imageId) {
        LOGGER.info("Creating Resource {} described as {} for Neighborhood {}", title, description, neighborhoodId);

        return resourceDao.createResource(neighborhoodId, title, description, imageId == null ? 0 : imageId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Resource> findResource(long neighborhoodId, long resourceId) {
        LOGGER.info("Finding Resource {} from Neighborhood {}", resourceId, neighborhoodId);

        return resourceDao.findResource(neighborhoodId, resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResources(long neighborhoodId, int page, int size) {
        LOGGER.info("Getting Resources from Neighborhood {}", neighborhoodId);

        return resourceDao.getResources(neighborhoodId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int countResources(long neighborhoodId) {
        LOGGER.info("Counting Contacts for Neighborhood {}", neighborhoodId);

        return resourceDao.countResources(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Resource updateResource(long neighborhoodId, long resourceId, String title, String description, Long imageId) {
        LOGGER.info("Updating Resource {} from Neighborhood {}", resourceId, neighborhoodId);

        Resource resource = resourceDao.findResource(neighborhoodId, resourceId).orElseThrow(NotFoundException::new);

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
    public boolean deleteResource(long neighborhoodId, long resourceId) {
        LOGGER.info("Deleting Resource {} from Neighborhood {}", resourceId, neighborhoodId);

        return resourceDao.deleteResource(neighborhoodId, resourceId);
    }
}

