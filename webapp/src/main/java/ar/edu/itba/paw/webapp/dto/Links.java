package ar.edu.itba.paw.webapp.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Links {
    private URI self;

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
