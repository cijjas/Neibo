package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
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
import java.util.Optional;

@Service
@Transactional
public class ResourceServiceImpl implements ResourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final ResourceDao resourceDao;
    private final NeighborhoodDao neighborhoodDao;
    private final ImageService imageService;

    @Autowired
    public ResourceServiceImpl(final ResourceDao resourceDao, ImageService imageService, NeighborhoodDao neighborhoodDao) {
        this.resourceDao = resourceDao;
        this.imageService = imageService;
        this.neighborhoodDao = neighborhoodDao;
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

    public Optional<Resource> findResource(long resourceId) {
        LOGGER.info("Finding Resource {}", resourceId);

        ValidationUtils.checkResourceId(resourceId);

        return resourceDao.findResource(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getResources(final long neighborhoodId) {
        LOGGER.info("Getting Resources from Neighborhood {}", neighborhoodId);

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        neighborhoodDao.findNeighborhood(neighborhoodId).orElseThrow(NotFoundException::new);

        return resourceDao.getResources(neighborhoodId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Resource updateResource(long resourceId, String title, String description, MultipartFile image) {
        LOGGER.info("Updating Resource {}", resourceId);

        Resource resource = findResource(resourceId).orElseThrow(()-> new NotFoundException("Resource Not Found"));

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

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteResource(final long resourceId) {
        LOGGER.info("Deleting Resource {}", resourceId);

        ValidationUtils.checkResourceId(resourceId);

        return resourceDao.deleteResource(resourceId);
    }
}

