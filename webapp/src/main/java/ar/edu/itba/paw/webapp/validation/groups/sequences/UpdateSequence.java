package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Specific;
import ar.edu.itba.paw.webapp.validation.groups.URI;

import javax.validation.GroupSequence;

@GroupSequence({Basic.class, URI.class, Specific.class, Authorization.class})
public interface UpdateSequence {
}
