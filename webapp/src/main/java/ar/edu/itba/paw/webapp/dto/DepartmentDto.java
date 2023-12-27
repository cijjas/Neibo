package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Department;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class DepartmentDto {

    private ar.edu.itba.paw.enums.Department department;
    private URI self;

    public static DepartmentDto fromDepartment(ar.edu.itba.paw.enums.Department department, UriInfo uriInfo){
        final DepartmentDto dto = new DepartmentDto();

        dto.department = department;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(department.getId()))
                .build();

        return dto;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public ar.edu.itba.paw.enums.Department getDepartment() {
        return department;
    }

    public void setDepartment(ar.edu.itba.paw.enums.Department department) {
        this.department = department;
    }
}
