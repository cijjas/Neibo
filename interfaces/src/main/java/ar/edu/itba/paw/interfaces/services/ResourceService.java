package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.models.Entities.Neighborhood;
import ar.edu.itba.paw.models.Entities.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Resource createResource(long neighborhoodId, String title, String description, MultipartFile imageFile);

    // -----------------------------------------------------------------------------------------------------------------

    List<Resource> getResources(final long neighborhoodId);

    Optional<Resource> findResource(final long resourceId, final long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Resource updateResource(long resourceId, String title, String description, MultipartFile image);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteResource(final long resourceId);
}
