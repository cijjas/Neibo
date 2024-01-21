package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceDao {

    // --------------------------------------------- RESOURCES INSERT --------------------------------------------------

    Resource createResource(long neighborhoodId, String title, String description, long imageId);

    // --------------------------------------------- RESOURCES SELECT --------------------------------------------------

    List<Resource> getResources(final long neighborhoodId);

    // --------------------------------------------- RESOURCE DELETE ----------------------------------------------------

    boolean deleteResource(final long resourceId);

    Optional<Resource> findResource(final long resourceId);
}
