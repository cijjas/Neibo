package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.CommentDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private UserDao userDao;

    private final String COMMENTS_JOIN_NEIGHBORS =
            "select postid, commentid, comment, commentdate, u.userid, name, surname, mail \n" +
            "from comments join public.users u on comments.userid = u.userid ";

    @Autowired
    public CommentDaoImpl(final DataSource ds, UserDao userDao) {
        this.userDao = userDao;
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("commentid")
                .withTableName("comments");
    }

    @Override
    public Comment createComment(String comment, long userId, long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("comment", comment);
        data.put("commentdate", Timestamp.valueOf(LocalDateTime.now()));
        data.put("userid", userId);
        data.put("postid", postId);

        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Comment.Builder()
                .commentId(key.longValue())
                .comment(comment)
                .postId(postId)
                .build();
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

    @Override
    public Optional<List<Comment>> findCommentsByPostId(long id) {
        final List<Comment> comments = jdbcTemplate.query( COMMENTS_JOIN_NEIGHBORS + " WHERE postid=?;", ROW_MAPPER, id);
        return comments.isEmpty() ? Optional.empty() : Optional.of(comments);
    }
}
