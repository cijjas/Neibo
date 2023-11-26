package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.MainEntities.Profession;

import java.util.List;

public interface ProfessionWorkerService {

    void addWorkerProfession(long workerId, long professionId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Profession> getWorkerProfessions(long workerId);

    String getWorkerProfessionsAsString(long workerId);

    String createURLForProfessionFilter(String professions, String currentUrl, long neighborhoodId);
}
