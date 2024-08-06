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

import static ar.edu.itba.paw.persistence.TestConstants.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ImageDaoImplTest {

    private final String IMAGE_NAME = "image";
    private final String IMAGE_FILE_NAME = "fake.jpg";
    private final String IMAGE_CONTENT_TYPE = "image/jpeg";
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ImageDao imageDaoImpl;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() throws IOException {
        // Pre Conditions
        byte[] fakeImageBytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        MockMultipartFile fakeImage = new MockMultipartFile(IMAGE_NAME, IMAGE_FILE_NAME, IMAGE_CONTENT_TYPE, fakeImageBytes);

        // Exercise
        Image image = imageDaoImpl.storeImage(fakeImage.getInputStream());

        // Validations & Post Conditions
        em.flush();
        assertNotNull(image);
        assertArrayEquals(fakeImageBytes, image.getImage());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.images.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_imageId_valid() {
        // Pre Conditions
        long iKey = testInserter.createImage();

        // Exercise
        Optional<Image> optionalImage = imageDaoImpl.findImage(iKey);

        // Validations & Post Conditions
        assertTrue(optionalImage.isPresent());
        assertEquals(iKey, optionalImage.get().getImageId().longValue());
    }

    @Test
    public void find_imageId_invalid_imageId() {
        // Pre Conditions
        long iKey = testInserter.createImage();

        // Exercise
        Optional<Image> optionalImage = imageDaoImpl.findImage(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalImage.isPresent());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
	public void delete_imageId_valid() {
	    // Pre Conditions
        long iKey = testInserter.createImage();

	    // Exercise
	    boolean deleted = imageDaoImpl.deleteImage(iKey);

	    // Validations & Post Conditions
		em.flush();
	    assertTrue(deleted);
	    assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.images.name()));
	}

    @Test
	public void delete_imageId_invalid_imageId() {
	    // Pre Conditions

	    // Exercise
	    boolean deleted = imageDaoImpl.deleteImage(INVALID_ID);

	    // Validations & Post Conditions
		em.flush();
	    assertFalse(deleted);
	}
}
