package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.interfaces.persistence.ImageDao;
import ar.edu.itba.paw.models.Entities.Image;
import ar.edu.itba.paw.persistence.TestInserter;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ImageDaoImplTest {

    private final String NAME = "image";
    private final String FILE_NAME = "fake.jpg";
    private final String CONTENT_TYPE = "image/jpeg";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ImageDao imageDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() throws IOException {
        // Pre Conditions
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        MockMultipartFile fakeImage = new MockMultipartFile(NAME, FILE_NAME, CONTENT_TYPE, fakeImageBytes);

        // Exercise
        Image storedImage = imageDao.storeImage(fakeImage.getInputStream());

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.images.name()));
        assertArrayEquals(fakeImageBytes, storedImage.getImage());
    }

    @Test
    public void find_imageId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();

        // Exercise
        Optional<Image> maybeImage = imageDao.findImage(iKey);

        // Validations & Post Conditions
        assertTrue(maybeImage.isPresent());
    }

    @Test
    public void find_imageId_invalid_imageId() {
        // Pre Conditions

        // Exercise
        Optional<Image> maybeImage = imageDao.findImage(1);

        // Validations & Post Conditions
        assertFalse(maybeImage.isPresent());
    }

    @Test
	public void delete_imageId_valid() {
	    // Pre Conditions
        long iKey = testInserter.createImage();

	    // Exercise
	    boolean deleted = imageDao.deleteImage(iKey);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.images.name()));
	}

    @Test
	public void delete_imageId_invalid_imageId() {
	    // Pre Conditions

	    // Exercise
	    boolean deleted = imageDao.deleteImage(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}
}
