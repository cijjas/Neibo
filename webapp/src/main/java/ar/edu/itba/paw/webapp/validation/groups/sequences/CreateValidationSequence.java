package ar.edu.itba.paw.webapp.validation.groups.sequences;

import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Form;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.Reference;

import javax.validation.GroupSequence;

@GroupSequence({Null.class, Form.class, Reference.class, Authorization.class})
public interface CreateValidationSequence {}
