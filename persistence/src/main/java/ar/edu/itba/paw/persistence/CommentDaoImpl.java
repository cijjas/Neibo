package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentDaoImpl implements CommentDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert; // En vez de hacer queries de tipo INSERT, usamos este objeto.

    @Autowired // Motor de inyección de dependencias; nos da el DataSource definido en el @Bean de WebConfig.
    public CommentDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("commentid")
                .withTableName("comments");
        // con .usingColumns(); podemos especificar las columnas a usar y otras cosas
    }

    private static final RowMapper<Comment> ROW_MAPPER =
            (rs, rowNum) -> new Comment.Builder()
                    .commentId(rs.getLong("commentid"))
                    .comment(rs.getString("comment"))
                    .date(rs.getDate("commentdate"))
                    .neighborId(rs.getLong("neighborid"))
                    .postId(rs.getLong("postid"))
                    .build();


    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        final List<Comment> comments = jdbcTemplate.query("SELECT * FROM comments WHERE postid=?;", ROW_MAPPER, id);
        System.out.println("AAA");
        System.out.println(comments);
        System.out.println("BBB");
        return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
    }
}
