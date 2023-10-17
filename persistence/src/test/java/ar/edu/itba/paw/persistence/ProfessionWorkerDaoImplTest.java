package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.paw.enums.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import java.util.List;

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
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_professions.name()));
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
        List<String> profession = professionWorkerDao.getWorkerProfessions(uKey.longValue());

        // Validations & Post Conditions
        assertFalse(profession.isEmpty());
    }
}
