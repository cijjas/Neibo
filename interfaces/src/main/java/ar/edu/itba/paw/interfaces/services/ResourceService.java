package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    Resource createResource(long neighborhoodId, String title, String description, Long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Resource> findResource(final long neighborhoodId, final long resourceId);

    List<Resource> getResources(final long neighborhoodId, int page, int size);

    int calculateResourcePages(long neighborhoodId, int size);

    // -----------------------------------------------------------------------------------------------------------------

    Resource updateResource(long resourceId, String title, String description, Long imageId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteResource(final long neighborhoodId, final long resourceId);
}
