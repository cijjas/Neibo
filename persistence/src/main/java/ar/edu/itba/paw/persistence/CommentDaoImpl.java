package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentDaoImpl implements CommentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String COMMENTS_JOIN_USERS =
            "SELECT postid, commentid, comment, commentdate, u.userid, name, surname, mail \n" +
                    "FROM comments " +
                    "INNER JOIN public.users u ON comments.userid = u.userid ";
    private final String COMMENTS = "SELECT * FROM comments ";
    private final String COUNT_COMMENTS = "SELECT COUNT(*) FROM comments";
    private UserDao userDao;
    private final RowMapper<Comment> ROW_MAPPER = (rs, rowNum) -> {
        User user = userDao.findUserById(rs.getLong("userid")).orElseThrow(() -> new NotFoundException("User Not Found"));

        return new Comment.Builder()
                .commentId(rs.getLong("commentid"))
                .comment(rs.getString("comment"))
                .date(rs.getDate("commentdate"))
                .postId(rs.getLong("postid"))
                .user(user)
                .build();
    };

    // -------------------------------------------- COMMENTS INSERT ----------------------------------------------------

    @Autowired
    public CommentDaoImpl(final DataSource ds, UserDao userDao) {
        this.userDao = userDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("commentid")
                .withTableName("comments");
    }

    // -------------------------------------------- COMMENTS SELECT ----------------------------------------------------

    @Override
    public Comment createComment(String comment, long userId, long postId) {
        LOGGER.debug("Inserting Comment {}", comment);
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

    @Override
    public Optional<Comment> findCommentById(long commentId) {
        LOGGER.debug("Selecting Comments from with commentId {}", commentId);
        final List<Comment> list = jdbcTemplate.query(COMMENTS + " WHERE commentid = ?", ROW_MAPPER, commentId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // Method cant properly differentiate between not finding the post and the post having no comments, but as the function
    // is called through the detail of a post it cant be an invalid postId
    @Override
    public List<Comment> getCommentsByPostId(long id, int page, int size) {
        LOGGER.debug("Selecting Comments from Post {}", id);
        return jdbcTemplate.query(COMMENTS_JOIN_USERS + " WHERE postid = ? ORDER BY commentdate DESC LIMIT ? OFFSET ?", ROW_MAPPER, id, size, (page - 1) * size);
    }

    @Override
    public int getCommentsCountByPostId(long id) {
        LOGGER.debug("Selecting Comments Count from Post {}", id);
        return jdbcTemplate.queryForObject(COUNT_COMMENTS + " WHERE postid = ?", Integer.class, id);
    }


}
