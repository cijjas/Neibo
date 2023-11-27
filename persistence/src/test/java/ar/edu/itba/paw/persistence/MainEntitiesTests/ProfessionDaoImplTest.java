package ar.edu.itba.paw.persistence.MainEntitiesTests;

import ar.edu.itba.paw.enums.Professions;
import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Profession;
import ar.edu.itba.paw.persistence.TestInserter;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class ProfessionDaoImplTest {
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ar.edu.itba.paw.persistence.MainEntitiesDaos.ProfessionDaoImpl professionDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateProfession() {
        // Pre Conditions

        // Exercise
        Profession profession = professionDao.createProfession(Professions.PLUMBER);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.professions.name()));
    }
}
