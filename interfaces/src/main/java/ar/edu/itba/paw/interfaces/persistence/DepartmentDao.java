package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.models.MainEntities.Department;

public interface DepartmentDao {

    // ------------------------------------------- DEPARTMENTS INSERT --------------------------------------------------

    Department createDepartment(ar.edu.itba.paw.enums.Department departments);
}
