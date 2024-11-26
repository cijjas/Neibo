package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Entities.Profession;

import java.util.List;
import java.util.Optional;

public interface ProfessionService {

    Profession createProfession(String name);

    // -----------------------------------------------------------------------------------------------------------------

    Optional<Profession> findProfession(long professionId);

    List<Profession> getProfessions(Long workerId);

    // -----------------------------------------------------------------------------------------------------------------

    boolean deleteProfession(long professionId);
}
