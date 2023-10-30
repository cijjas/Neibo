package ar.edu.itba.paw.interfaces.services;

import java.util.List;

public interface ProfessionWorkerService {

    void addWorkerProfession(long workerId, long professionId);

    // -----------------------------------------------------------------------------------------------------------------

    List<String> getWorkerProfessions(long workerId);

    String getWorkerProfessionsAsString(long workerId);

    public String createURLForProfessionFilter(String professions, String currentUrl, long neighborhoodId);
}
