package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.models.Profession;
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
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql("classpath:hsqlValueCleanUp.sql")
public class ProfessionDaoImplTest {

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;
    private TestInsertionUtils testInsertionUtils;
    private ProfessionDao professionDao;

    private String PROFESSION_1 = "Profession 1";
    private String PROFESSION_2 = "Profession 2";
    private String PROFESSION_3 = "Profession 3";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        testInsertionUtils = new TestInsertionUtils(jdbcTemplate, ds);
        professionDao = new ProfessionDaoImpl(ds);
    }

    @Test
    public void testGetProfessions() {
        // Pre Conditions
        testInsertionUtils.createProfession(PROFESSION_1);
        testInsertionUtils.createProfession(PROFESSION_2);
        testInsertionUtils.createProfession(PROFESSION_3);

        // Exercise
        List<Profession> professions = professionDao.getProfessions();

        // Validations & Post Conditions
        assertNotNull(professions);
        assertEquals(3, professions.size());
    }
}
