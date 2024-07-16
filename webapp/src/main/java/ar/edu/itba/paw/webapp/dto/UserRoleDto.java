package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.UserRole;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserRoleDto {

    private UserRole userRole;
    private Links _links;

    public static UserRoleDto fromUserRole(UserRole userRole, UriInfo uriInfo) {
        final UserRoleDto dto = new UserRoleDto();

        dto.userRole = userRole;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("user-roles")
                .path(String.valueOf(userRole.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
