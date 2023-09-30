package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.Channel;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final String IMAGES = "SELECT * FROM images ";
    @Autowired
    public ImageDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("imageid")
                .withTableName("images");
    }

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    @Override
    public Image storeImage(MultipartFile image) {
        Map<String, Object> data = new HashMap<>();
        byte[] imageBytes = null;
        try {
            imageBytes = image.getBytes();
        } catch (IOException e) {
            System.out.println("Issue uploading the image");
        }
        data.put("image", imageBytes);
        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new Image.Builder()
                .imageId(key.longValue())
                .image(imageBytes)
                .build();
    }

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    private static final RowMapper<Image> ROW_MAPPER = (rs, rowNum) ->
            new Image.Builder()
                    .imageId(rs.getLong("imageid"))
                    .image(rs.getBytes("image"))
                    .build();

    @Override
    public Optional<Image> getImage(long imageId) {
        final List<Image> list = jdbcTemplate.query(IMAGES + " WHERE imageid = ?", ROW_MAPPER, imageId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
