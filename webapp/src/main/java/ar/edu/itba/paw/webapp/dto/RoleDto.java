package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.UserRole;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class RoleDto {
    private UserRole userRole;

    public static RoleDto fromRole(UserRole userRole, UriInfo uriInfo){
        final RoleDto dto = new RoleDto();

        dto.userRole = userRole;

        return dto;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
