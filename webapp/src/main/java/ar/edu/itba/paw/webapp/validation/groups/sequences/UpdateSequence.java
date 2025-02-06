package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;
import ar.edu.itba.paw.webapp.validation.groups.Specific;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({OnUpdate.class, Default.class, Specific.class})
public interface UpdateSequence {
}
