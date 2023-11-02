package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.enums.Departments;
import ar.edu.itba.paw.models.MainEntities.Department;

public interface DepartmentDao {
    Department createDepartment(Departments departments);
}
