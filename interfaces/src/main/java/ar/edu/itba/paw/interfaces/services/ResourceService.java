package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    Resource createResource(long neighborhoodId, String title, String description, MultipartFile imageFile);

    // -----------------------------------------------------------------------------------------------------------------

    List<Resource> getResources(final long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteResource(final long resourceId);
}
