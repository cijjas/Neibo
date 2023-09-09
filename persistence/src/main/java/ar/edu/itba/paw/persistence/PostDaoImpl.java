package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.models.Neighbor;
import ar.edu.itba.paw.models.Neighborhood;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;

@Repository
public class PostDaoImpl implements PostDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public PostDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("postid")
                .withTableName("posts");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    @Override
    public Post create(String title, String description, long neighborId) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("postdate", LocalDate.now());
        data.put("neighborid", neighborId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Post.Builder()
                .postId(key.longValue())
                .title(title)
                .description(description)
                .build();
    }

    private static final RowMapper<Post> ROW_MAPPER =
            (rs, rowNum) -> new Post.Builder()
                            .postId(rs.getLong("postid"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .date(rs.getDate("postdate"))
                            .neighbor(
                                    new Neighbor.Builder()
                                    .neighborId(rs.getLong("neighborid"))
                                    .mail(rs.getString("mail"))
                                    .name(rs.getString("name"))
                                    .surname(rs.getString("surname"))
                                    .neighborhood(
                                                new Neighborhood.Builder()
                                                .neighborhoodId(rs.getLong("neighborhoodid"))
                                                .name(rs.getString("neighborhoodname"))
                                                .build()
                                    ).build()
                            ).build();

    @Override
    public List<Post> getAllPosts() {
        String sqlQuery = "select postid, title, description, postdate, n.neighborid, mail, name, surname, n.neighborhoodid,neighborhoodname from\n" +
                "             posts p join neighbors n on p.neighborid = n.neighborid join neighborhoods nh on n.neighborhoodid = nh.neighborhoodid;";

        final List<Post> postList = jdbcTemplate.query(sqlQuery, ROW_MAPPER);

        return postList;
    }
}
