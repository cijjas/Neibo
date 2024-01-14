package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.models.Entities.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- DEPARTMENT INSERT ------------------------------------------------

    @Override
    public Department createDepartment(ar.edu.itba.paw.enums.Department departmentType) {
        LOGGER.debug("Inserting Department {}", departmentType);
        final Department department = new Department.Builder()
                .department(departmentType)
                .build();
        em.persist(department);
        return department;
    }

    @Override
    public Optional<Department> findDepartment(long departmentId) {
        LOGGER.debug("Selecting Department with id {}", departmentId);
        return Optional.ofNullable(em.find(Department.class, departmentId));
    }
}
