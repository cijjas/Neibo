package ar.edu.itba.paw.persistence.MainEntitiesDaos;

import ar.edu.itba.paw.enums.Departments;
import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.models.MainEntities.Department;
import ar.edu.itba.paw.models.MainEntities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDaoImpl.class);
    @PersistenceContext
    private EntityManager em;

    // ---------------------------------------------- DEPARTMENT INSERT ------------------------------------------------

    @Override
    public Department createDepartment(Departments departmentType) {
        LOGGER.debug("Inserting Department {}", departmentType);
        final Department department = new Department.Builder()
                .department(departmentType)
                .build();
        em.persist(department);
        return department;
    }
}
