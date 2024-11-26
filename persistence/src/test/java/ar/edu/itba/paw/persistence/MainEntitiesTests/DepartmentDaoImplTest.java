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
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.TestConstants.*;
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
    private ar.edu.itba.paw.persistence.MainEntitiesDaos.DepartmentDaoImpl departmentDaoImpl;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    // ------------------------------------------------- CREATE --------------------------------------------------------

    @Test
    public void create_valid() {
        // Pre Conditions

        // Exercise
        Department department = departmentDaoImpl.createDepartment(DEPARTMENT_NAME_1);

        // Validations & Post Conditions
        em.flush();
        assertNotNull(department);
        assertEquals(DEPARTMENT_NAME_1, department.getDepartment());
        assertEquals(ONE_ELEMENT, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.departments.name()));
    }

    // -------------------------------------------------- FINDS --------------------------------------------------------

    @Test
    public void find_departmentId_valid() {
        // Pre Conditions
        long dKey = testInserter.createDepartment();

        // Exercise
        Optional<Department> optionalDepartment = departmentDaoImpl.findDepartment(dKey);

        // Validations & Post Conditions
        assertTrue(optionalDepartment.isPresent());
        assertEquals(dKey, optionalDepartment.get().getDepartmentId().longValue());
    }

    @Test
    public void find_departmentId_invalid_departmentId() {
        // Pre Conditions
        long dKey = testInserter.createDepartment();

        // Exercise
        Optional<Department> optionalDepartment = departmentDaoImpl.findDepartment(INVALID_ID);

        // Validations & Post Conditions
        assertFalse(optionalDepartment.isPresent());
    }

    // -------------------------------------------------- GETS ---------------------------------------------------------

    @Test
    public void get() {
        // Pre Conditions
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);
        long dKey2 = testInserter.createDepartment(DEPARTMENT_NAME_2);

        // Exercise
        List<Department> departmentList = departmentDaoImpl.getDepartments();

        // Validations & Post Conditions
        assertEquals(TWO_ELEMENTS, departmentList.size());
    }

    @Test
    public void get_empty() {
        // Pre Conditions

        // Exercise
        List<Department> departmentList = departmentDaoImpl.getDepartments();

        // Validations & Post Conditions
        assertTrue(departmentList.isEmpty());
    }

    // ------------------------------------------------ DELETES --------------------------------------------------------

    @Test
    public void delete_professionId_valid() {
        // Pre Conditions
        long dKey1 = testInserter.createDepartment(DEPARTMENT_NAME_1);

        // Exercise
        boolean deleted = departmentDaoImpl.deleteDepartment(dKey1);

        // Validations & Post Conditions
        em.flush();
        assertTrue(deleted);
        assertEquals(NO_ELEMENTS, JdbcTestUtils.countRowsInTable(jdbcTemplate, Table.professions.name()));
    }

    @Test
    public void delete_professionId_invalid_professionId() {
        // Pre Conditions

        // Exercise
        boolean deleted = departmentDaoImpl.deleteDepartment(INVALID_ID);

        // Validations & Post Conditions
        em.flush();
        assertFalse(deleted);
    }
}
