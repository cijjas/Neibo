package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfessionWorkerServiceImpl implements ProfessionWorkerService {
    private final ProfessionWorkerDao professionWorkerDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionWorkerServiceImpl.class);

    @Autowired
    public ProfessionWorkerServiceImpl(ProfessionWorkerDao professionWorkerDao) {
        this.professionWorkerDao = professionWorkerDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void addWorkerProfession(long workerId, long professionId) {
        LOGGER.info("Adding Profession {} to Worker {}", professionId, workerId);
        professionWorkerDao.addWorkerProfession(workerId, professionId);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<String> getWorkerProfessions(long workerId) {
        LOGGER.info("Adding Professions for Worker {}", workerId);
        return professionWorkerDao.getWorkerProfessions(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getWorkerProfessionsAsString(long workerId) {
        List<String> professions = getWorkerProfessions(workerId);
        StringBuilder professionsString = new StringBuilder();
        for (String profession : professions) {
            if (professionsString.length() > 0) {
                professionsString.append(", ");
            }
            professionsString.append(profession);
        }
        return professionsString.toString();
    }
}
