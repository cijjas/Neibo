package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.enums.Language;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class LanguageDto {

    private Language name;

    private Links _links;

    public static LanguageDto fromLanguage(Language language, UriInfo uriInfo) {
        final LanguageDto dto = new LanguageDto();

        dto.name = language;

        Links links = new Links();

        String languageId = String.valueOf(language.getId());

        UriBuilder languageUri = uriInfo.getBaseUriBuilder().path(Endpoint.LANGUAGES.toString()).path(languageId);

        links.setSelf(languageUri.build());

        dto.set_links(links);
        return dto;
    }

    public Language getName() {
        return name;
    }

    public void setName(Language name) {
        this.name = name;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
