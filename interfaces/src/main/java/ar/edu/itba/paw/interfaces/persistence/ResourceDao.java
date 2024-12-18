package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceDao {

    // --------------------------------------------- RESOURCES INSERT --------------------------------------------------

    Resource createResource(long neighborhoodId, String title, String description, long imageId);

    // --------------------------------------------- RESOURCES SELECT --------------------------------------------------

    Optional<Resource> findResource(long resourceId);

    List<Resource> getResources(long neighborhoodId, int page, int size);

    int countResources(long neighborhoodId);

    // --------------------------------------------- RESOURCE DELETE ----------------------------------------------------

    boolean deleteResource(long neighborhoodId, long resourceId);

}
