package ar.edu.itba.paw.persistence.MainEntitiesTests;


import ar.edu.itba.paw.enums.Table;
import ar.edu.itba.paw.models.Entities.Department;
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

import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.INVALID_ID;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, TestInserter.class})
@Transactional
@Rollback
public class DepartmentDaoImplTest {
    @Autowired
    private DataSource ds;
    @Autowired
    private TestInserter testInserter;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ar.edu.itba.paw.persistence.MainEntitiesDaos.DepartmentDaoImpl departmentDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Department department = departmentDao.createDepartment(ar.edu.itba.paw.enums.Department.ELECTRONICS);

        // Validations & Post Conditions
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.departments.name()));
    }

    @Test
	public void find_departmentId_valid() {
	    // Pre Conditions
        long dKey = testInserter.createDepartment();

	    // Exercise
	    Optional<Department> optional = departmentDao.findDepartment(dKey);

	    // Validations & Post Conditions
	    assertTrue(optional.isPresent());
		assertEquals(dKey, optional.get().getDepartmentId().longValue());
	}

    @Test
	public void find_departmentId_invalid_departmentId() {
	    // Pre Conditions

	    // Exercise
	    Optional<Department> optional = departmentDao.findDepartment(INVALID_ID);

	    // Validations & Post Conditions
	    assertFalse(optional.isPresent());
	}
}
