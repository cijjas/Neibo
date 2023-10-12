package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionWorkerServiceImpl implements ProfessionWorkerService {
    private final ProfessionWorkerDao professionWorkerDao;

    @Autowired
    public ProfessionWorkerServiceImpl(ProfessionWorkerDao professionWorkerDao) {
        this.professionWorkerDao = professionWorkerDao;
    }

    // --------------------------------------- PROFESSIONWORKERS INSERT ------------------------------------------------
    @Override
    public void addWorkerProfession(long workerId, long professionId) {
        professionWorkerDao.addWorkerProfession(workerId, professionId);
    }

    // --------------------------------------- PROFESSIONWORKERS SELECT ------------------------------------------------
    @Override
    public List<String> getWorkerProfessions(long workerId) {
        return professionWorkerDao.getWorkerProfessions(workerId);
    }
}
