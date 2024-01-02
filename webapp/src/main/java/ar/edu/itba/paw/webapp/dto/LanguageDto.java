package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Language;
import ar.edu.itba.paw.enums.UserRole;

import javax.ws.rs.core.UriInfo;

public class LanguageDto {
    private Language language;

    public static LanguageDto fromLanguage(Language language, UriInfo uriInfo){
        final LanguageDto dto = new LanguageDto();

        dto.language = language;

        return dto;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
