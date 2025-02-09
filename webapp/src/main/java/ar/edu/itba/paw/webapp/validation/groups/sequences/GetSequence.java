package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, Specific.class})
public interface GetSequence {
}
