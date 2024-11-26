package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.models.Entities.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- DEPARTMENT INSERT ------------------------------------------------

    @Override
    public Department createDepartment(String departmentName) {
        LOGGER.debug("Inserting Department {}", departmentName);

        final Department department = new Department.Builder()
                .department(departmentName)
                .build();
        em.persist(department);
        return department;
    }

    // ---------------------------------------------- DEPARTMENT SELECT ------------------------------------------------

    @Override
    public Optional<Department> findDepartment(long departmentId) {
        LOGGER.debug("Selecting Department with id {}", departmentId);

        return Optional.ofNullable(em.find(Department.class, departmentId));
    }


    @Override
    public List<Department> getDepartments() {
        LOGGER.debug("Selecting Departments");

        String query = "SELECT d FROM Department d"; // JPQL query
        return em.createQuery(query, Department.class).getResultList();
    }


    // ---------------------------------------------- DEPARTMENT DELETE ------------------------------------------------

    @Override
    public boolean deleteDepartment(long departmentId) {
        LOGGER.debug("Deleting Department with id {}", departmentId);

        Department department = em.find(Department.class, departmentId);
        if (department != null) {
            em.remove(department);
            return true;
        }
        return false;
    }
}
