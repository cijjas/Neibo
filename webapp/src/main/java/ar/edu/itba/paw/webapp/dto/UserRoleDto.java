package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.TransactionType;
import ar.edu.itba.paw.enums.UserRole;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserRoleDto {

    private UserRole userRole;

    private URI self;

    public static UserRoleDto fromUserRole(UserRole userRole, UriInfo uriInfo){
        final UserRoleDto dto = new UserRoleDto();

        dto.userRole = userRole;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("user-roles")
                .path(String.valueOf(userRole.getId()))
                .build();

        return dto;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
