package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.exceptions.InsertionException;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.MainEntities.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageDaoImpl implements ImageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageDaoImpl.class);
    private static final RowMapper<Image> ROW_MAPPER = (rs, rowNum) ->
            new Image.Builder()
                    .imageId(rs.getLong("imageid"))
                    .image(rs.getBytes("image"))
                    .build();
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String IMAGES = "SELECT * FROM images ";

    // --------------------------------------------- IMAGES INSERT -----------------------------------------------------

    @Autowired
    public ImageDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("imageid")
                .withTableName("images");
    }

    // --------------------------------------------- IMAGES SELECT -----------------------------------------------------

    @Override
    public Image storeImage(MultipartFile image) {
        LOGGER.debug("Inserting Image {}", image.getName());
        Map<String, Object> data = new HashMap<>();
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (IOException e) {
            LOGGER.error("Error whilst getting the Image bytes", e);
            throw new InsertionException("An error occurred whilst storing the image");
        }

        try {
            data.put("image", imageBytes);
            final Number key = jdbcInsert.executeAndReturnKey(data);
            return new Image.Builder()
                    .imageId(key.longValue())
                    .image(imageBytes)
                    .build();
        } catch (DataAccessException ex) {
            LOGGER.error("Error Inserting the Image", ex);
            throw new InsertionException("An error occurred whilst storing the image");
        }
    }

    @Override
    public Optional<Image> getImage(long imageId) {
        LOGGER.debug("Selecting Image with id {}", imageId);
        final List<Image> list = jdbcTemplate.query(IMAGES + " WHERE imageid = ?", ROW_MAPPER, imageId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
