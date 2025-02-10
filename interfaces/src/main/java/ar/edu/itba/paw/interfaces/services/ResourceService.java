package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Resource createResource(long neighborhoodId, String title, String description, Long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Resource> findResource(long neighborhoodId, long resourceId);

    List<Resource> getResources(long neighborhoodId, int page, int size);

    int countResources(long neighborhoodId);

    // -----------------------------------------------------------------------------------------------------------------

    Resource updateResource(long neighborhoodId, long resourceId, String title, String description, Long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteResource(long neighborhoodId, long resourceId);
}
