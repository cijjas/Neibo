package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.interfaces.services.ProfessionService;
import ar.edu.itba.paw.models.Entities.Profession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfessionServiceImpl implements ProfessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionServiceImpl.class);
    private final ProfessionDao professionDao;

    @Autowired
    public ProfessionServiceImpl(ProfessionDao professionDao) {
        this.professionDao = professionDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<Profession> getProfessions(String workerURN) {
        LOGGER.info("Getting Professions for Worker {}", workerURN);

        Long workerId = ValidationUtils.checkURNAndExtractWorkerId(workerURN);

        return professionDao.getProfessions(workerId);
    }


}
