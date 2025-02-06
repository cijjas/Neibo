package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.OnDelete;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({OnDelete.class, Default.class, Specific.class})
public interface DeleteSequence {
}
