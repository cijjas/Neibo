package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Department;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class DepartmentDto {

    @NotNull(groups = OnCreate.class)
    @Size(min = 0, max = 64)
    @Pattern(regexp = "[a-zA-Z0-9 ?!@_]*")
    private String name;

    private Links _links;

    public static DepartmentDto fromDepartment(Department department, UriInfo uriInfo) {
        final DepartmentDto dto = new DepartmentDto();

        dto.name = department.getDepartment();

        Links links = new Links();

        String departmentId = String.valueOf(department.getDepartmentId());

        UriBuilder departmentUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.DEPARTMENTS).path(departmentId);

        links.setSelf(departmentUri.build());

        dto.set_links(links);
        return dto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
