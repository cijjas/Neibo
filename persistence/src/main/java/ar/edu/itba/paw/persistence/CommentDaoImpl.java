package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentDaoImpl implements CommentDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private UserDao userDao;

    private final String COMMENTS_JOIN_USERS =
            "SELECT postid, commentid, comment, commentdate, u.userid, name, surname, mail \n" +
            "FROM comments JOIN public.users u ON comments.userid = u.userid ";
    private final String COMMENTS =
            "SELECT * FROM comments ";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);

    @Autowired
    public CommentDaoImpl(final DataSource ds, UserDao userDao) {
        this.userDao = userDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("commentid")
                .withTableName("comments");
    }

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    @Override
    public Comment createComment(String comment, long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("commentdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postid", postId);

        try {
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Comment.Builder()
                    .commentId(key.longValue())
                    .comment(comment)
                    .postId(postId)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error inserting the Comment", ex);
            throw new InsertionException("An error occurred whilst creating the Comment");
        }
    }

    private final RowMapper<Comment> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("userid")).orElse(null);

        return new Comment.Builder()
                .commentId(rs.getLong("commentid"))
                .comment(rs.getString("comment"))
                .date(rs.getDate("commentdate"))
                .postId(rs.getLong("postid"))
                .user(user)
                .build();
    };

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        final List<Comment> comments = jdbcTemplate.query( COMMENTS_JOIN_USERS + " WHERE postid=?;", ROW_MAPPER, id);
        return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
    }

    @Override
    public Optional<Comment> findCommentById(long commentId) {
        final List<Comment> list = jdbcTemplate.query(COMMENTS + " WHERE commentid = ?", ROW_MAPPER, commentId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
