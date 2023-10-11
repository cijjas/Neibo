package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ProfessionWorkerDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ProfessionWorkerDao professionWorkerDao;
    private String PROFESSION_NAME = "Argentinian President";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        professionWorkerDao = new ProfessionWorkerDaoImpl(ds);
    }

    @Test
    public void testAddWorkerProfession() {
        // Pre Conditions
        Number pKey = testInsertionUtils.createProfession();
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());

        // Exercise
        professionWorkerDao.addWorkerProfession(uKey.longValue(), pKey.longValue());

        // Validations & Post Conditions
        // No exceptions :D
    }

    @Test
    public void testGetWorkerProfession() {
        // Pre Conditions
        Number pKey = testInsertionUtils.createProfession(PROFESSION_NAME);
        Number nhKey = testInsertionUtils.createNeighborhood();
        Number uKey = testInsertionUtils.createUser(nhKey.longValue());
        testInsertionUtils.createWorker(uKey.longValue());
        testInsertionUtils.createWorkerProfession(uKey.longValue(), pKey.longValue());

        // Exercise
        String profession = professionWorkerDao.getWorkerProfession(uKey.longValue());

        // Validations & Post Conditions
        assertEquals(PROFESSION_NAME, profession);
    }

/*    @Test
    public void testGetNoWorkerProfession() {
        // Pre Conditions

        // Exercise
        String profession = professionWorkerDao.getWorkerProfession(1);

        // Validations & Post Conditions
        assertNull(PROFESSION_NAME);
    }*/
}
