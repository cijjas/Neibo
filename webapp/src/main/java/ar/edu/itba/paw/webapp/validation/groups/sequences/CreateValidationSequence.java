package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.*;

import javax.validation.GroupSequence;

@GroupSequence({Null.class, Basic.class, URN.class, Reference.class, Specific.class,Authorization.class})
public interface CreateValidationSequence {}
