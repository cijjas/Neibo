package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Post;
import org.springframework.stereotype.Repository;

@Repository
public class PostDaoImpl implements PostDao {
    @Override
    public Post create(String title, String description, int neighborId) {
        // BASE DE DATOS
        return null;
    }
}
