package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;

public interface ProfessionWorkerService {

    void addWorkerProfession(long workerId, long professionId);

    // -----------------------------------------------------------------------------------------------------------------

    List<Profession> getWorkerProfessions(String workerURN);

    // -----------------------------------------------------------------------------------------------------------------

    String createURLForProfessionFilter(String professions, String currentUrl, long neighborhoodId);
}
