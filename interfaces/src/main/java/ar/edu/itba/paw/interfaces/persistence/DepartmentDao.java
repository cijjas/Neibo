package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.Entities.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentDao {

    // ------------------------------------------- DEPARTMENTS INSERT --------------------------------------------------

    Department createDepartment(String departmentName);

    // ------------------------------------------- DEPARTMENTS SELECT --------------------------------------------------

    Optional<Department> findDepartment(long departmentId);

    List<Department> getDepartments();

    // ------------------------------------------- DEPARTMENTS DELETE --------------------------------------------------

    boolean deleteDepartment(long departmentId);
}
