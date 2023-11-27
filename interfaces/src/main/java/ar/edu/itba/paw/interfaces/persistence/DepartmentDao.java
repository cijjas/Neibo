package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Department;

import java.util.Optional;

public interface DepartmentDao {

    // ------------------------------------------- DEPARTMENTS INSERT --------------------------------------------------

    Department createDepartment(ar.edu.itba.paw.enums.Department departments);

    Optional<Department> findDepartmentById(long departmentId);
}
