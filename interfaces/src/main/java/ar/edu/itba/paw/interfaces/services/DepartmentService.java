package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Department;
import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    Department createDepartment(String departmentName);

    List<Department> getDepartments();

    Optional<Department> findDepartment(long departmentId);

    boolean deleteDepartment(long departmentId);
}
