package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Department;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;

public class DepartmentDto {

    @NotNull(groups = Null.class)
    @Size(min = 0, max = 64, groups = Basic.class)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*", groups = Basic.class)
    private String department;

    private Links _links;

    public static DepartmentDto fromDepartment(Department department, UriInfo uriInfo) {
        final DepartmentDto dto = new DepartmentDto();

        dto.department = department.getDepartment();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(department.getDepartmentId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
