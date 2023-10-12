package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.config.TestConfig;
import enums.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ImageDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ImageDao imageDao;

    private String NAME = "image";
    private String FILE_NAME = "fake.jpg";
    private String CONTENT_TYPE = "image/jpeg";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        imageDao = new ImageDaoImpl(ds);
    }

    @Test
    public void testStoreImageAndGetImage() {
        // Pre Conditions
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        MockMultipartFile fakeImage = new MockMultipartFile(NAME, FILE_NAME, CONTENT_TYPE, fakeImageBytes);

        // Exercise
        Image storedImage = imageDao.storeImage(fakeImage);

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.images.name()));
        assertArrayEquals(fakeImageBytes, storedImage.getImage());
    }

    @Test
    public void testFindImage(){
        // Pre Conditions
        long iKey = testInsertionUtils.createImage();

        // Exercise
        Optional<Image> maybeImage = imageDao.getImage(iKey);

        // Validations & Post Conditions
        assertTrue(maybeImage.isPresent());
    }
}
