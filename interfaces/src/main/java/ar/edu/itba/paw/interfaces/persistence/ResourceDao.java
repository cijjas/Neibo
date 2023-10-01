package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Resource;

import java.util.List;

public interface ResourceDao {
    Resource createResource(long neighborhoodId, String title, String description, long imageId);

    List<Resource> getResources(final long neighborhoodId);

    boolean deleteResource(final long resourceId);
}
