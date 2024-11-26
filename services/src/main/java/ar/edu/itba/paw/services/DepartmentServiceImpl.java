package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.DepartmentDao;
import ar.edu.itba.paw.interfaces.services.DepartmentService;
import ar.edu.itba.paw.models.Entities.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentDao departmentDao;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Department createDepartment(String departmentName) {
        LOGGER.info("Creating Department {}", departmentName);

        return departmentDao.createDepartment(departmentName);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<Department> findDepartment(long departmentId) {
        LOGGER.info("Finding Department {}", departmentId);

        return departmentDao.findDepartment(departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getDepartments() {
        LOGGER.info("Getting Departments");

        return departmentDao.getDepartments();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean deleteDepartment(long departmentId) {
        LOGGER.info("Deleting Department {}", departmentId);

        return departmentDao.deleteDepartment(departmentId);
    }
}
