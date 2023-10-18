package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Table;
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
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Sql("classpath:hsqlValueCleanUp.sql")
public class ProfessionWorkerDaoImplTest {

    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;

    private JdbcTemplate jdbcTemplate;
    private ProfessionWorkerDao professionWorkerDao;


    private final String PROFESSION_NAME = "Argentinian President";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        professionWorkerDao = new ProfessionWorkerDaoImpl(ds);
    }

    @Test
    public void testAddWorkerProfession() {
        // Pre Conditions
        Number pKey = testInserter.createProfession();
        Number nhKey = testInserter.createNeighborhood();
        Number uKey = testInserter.createUser(nhKey.longValue());

        // Exercise
        professionWorkerDao.addWorkerProfession(uKey.longValue(), pKey.longValue());

        // Validations & Post Conditions
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.workers_professions.name()));
    }

    @Test
    public void testGetWorkerProfession() {
        // Pre Conditions
        Number pKey = testInserter.createProfession(PROFESSION_NAME);
        Number nhKey = testInserter.createNeighborhood();
        Number uKey = testInserter.createUser(nhKey.longValue());
        testInserter.createWorker(uKey.longValue());
        testInserter.createWorkerProfession(uKey.longValue(), pKey.longValue());

        // Exercise
        List<String> profession = professionWorkerDao.getWorkerProfessions(uKey.longValue());

        // Validations & Post Conditions
        assertFalse(profession.isEmpty());
    }
}
