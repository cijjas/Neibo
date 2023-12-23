package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDto {
    private String mail;
    private String name;
    private URI self;
    private URI posts;

    public static UserDto fromUser(final User user, final UriInfo uriInfo){
        final UserDto dto = new UserDto();

        dto.mail = user.getMail();
        dto.name = user.getName();

        dto.self = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(user.getUserId()))
                .build();

        dto.posts = uriInfo.getBaseUriBuilder()
                .path("posts")
                .queryParam("postedBy", String.valueOf(user.getUserId()))
                .build();

        return dto;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
