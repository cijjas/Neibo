package ar.edu.itba.paw.webapp.dto;

import javax.ws.rs.core.UriInfo;

public class DepartmentDto {

    private ar.edu.itba.paw.enums.Department department;
    private Links _links;

    public static DepartmentDto fromDepartment(ar.edu.itba.paw.enums.Department department, UriInfo uriInfo) {
        final DepartmentDto dto = new DepartmentDto();

        dto.department = department;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("departments")
                .path(String.valueOf(department.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public ar.edu.itba.paw.enums.Department getDepartment() {
        return department;
    }

    public void setDepartment(ar.edu.itba.paw.enums.Department department) {
        this.department = department;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
