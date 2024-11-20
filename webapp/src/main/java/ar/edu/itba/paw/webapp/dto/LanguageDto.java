package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Language;

import javax.ws.rs.core.UriInfo;

public class LanguageDto {

    private Language language;

    private Links _links;

    public static LanguageDto fromLanguage(Language language, UriInfo uriInfo) {
        final LanguageDto dto = new LanguageDto();

        dto.language = language;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("languages")
                .path(String.valueOf(language.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
