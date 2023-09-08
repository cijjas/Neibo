package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.NeighborhoodDao;
import ar.edu.itba.paw.models.Neighborhood;
import org.springframework.stereotype.Repository;

@Repository
public class NeighborhoodDaoImpl implements NeighborhoodDao {
    @Override
    public Neighborhood create(String name) {
        // BASE DE DATOS
        return null;
    }
}
