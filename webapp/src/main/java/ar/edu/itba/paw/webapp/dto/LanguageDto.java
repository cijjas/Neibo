package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class LanguageDto {
    private Language language;
    private URI self;

    public static LanguageDto fromLanguage(Language language, UriInfo uriInfo){
        final LanguageDto dto = new LanguageDto();

        dto.language = language;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("languages")
                .path(String.valueOf(language.getId()))
                .build();

        return dto;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
