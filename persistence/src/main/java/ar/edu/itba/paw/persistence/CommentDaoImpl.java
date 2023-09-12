package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentDaoImpl implements CommentDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String COMMENTS = "SELECT * FROM comments ";

    @Autowired
    public CommentDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("commentid")
                .withTableName("comments");
    }

    @Override
    public Comment createComment(String comment, long neighborId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("commentdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("neighborid", neighborId);
        data.put("postid", postId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Comment.Builder()
                .commentId(key.longValue())
                .comment(comment)
                .neighborId(neighborId)
                .postId(postId)
                .build();
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
        final List<Comment> comments = jdbcTemplate.query( COMMENTS + " WHERE postid=?;", ROW_MAPPER, id);
        return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
    }
}
