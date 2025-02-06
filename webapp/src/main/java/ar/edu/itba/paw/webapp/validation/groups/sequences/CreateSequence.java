package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.*;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({OnCreate.class, Default.class, Specific.class})
public interface CreateSequence {
}
