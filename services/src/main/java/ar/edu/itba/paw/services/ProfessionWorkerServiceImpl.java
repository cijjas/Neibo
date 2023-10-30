package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import ar.edu.itba.paw.models.MainEntities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfessionWorkerServiceImpl implements ProfessionWorkerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerServiceImpl.class);
    private final ProfessionWorkerDao professionWorkerDao;

    @Autowired
    public ProfessionWorkerServiceImpl(ProfessionWorkerDao professionWorkerDao) {
        this.professionWorkerDao = professionWorkerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addWorkerProfession(long workerId, long professionId) {
        LOGGER.info("Adding Profession {} to Worker {}", professionId, workerId);
        professionWorkerDao.createSpecialization(workerId, professionId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Profession> getWorkerProfessions(long workerId) {
        LOGGER.info("Adding Professions for Worker {}", workerId);
        return professionWorkerDao.getWorkerProfessions(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getWorkerProfessionsAsString(long workerId) {
        List<Profession> professions = getWorkerProfessions(workerId);
        StringBuilder professionsString = new StringBuilder();
        for (Profession profession : professions) {
            if (professionsString.length() > 0) {
                professionsString.append(", ");
            }
            professionsString.append(profession.toString());
        }
        return professionsString.toString();
    }
}
