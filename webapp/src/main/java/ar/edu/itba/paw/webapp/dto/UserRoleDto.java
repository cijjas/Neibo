package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.UserRole;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class UserRoleDto {

    private UserRole role;

    private Links _links;

    public static UserRoleDto fromUserRole(UserRole userRole, UriInfo uriInfo) {
        final UserRoleDto dto = new UserRoleDto();

        dto.role = userRole;

        Links links = new Links();

        String userRoleId = String.valueOf(userRole.getId());

        UriBuilder userRoleUri = uriInfo.getBaseUriBuilder().path(Endpoint.USER_ROLES).path(userRoleId);

        links.setSelf(userRoleUri.build());

        dto.set_links(links);
        return dto;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
