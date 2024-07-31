package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;

public interface ProfessionService {

    List<Profession> getProfessions(String workerURN);
}
