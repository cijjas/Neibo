package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.ProfessionWorkerDao;
import ar.edu.itba.paw.interfaces.services.ProfessionWorkerService;
import ar.edu.itba.paw.models.Entities.Profession;
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
        LOGGER.info("Getting Professions for Worker {}", workerId);

        ValidationUtils.checkWorkerId(workerId);

        return professionWorkerDao.getWorkerProfessions(workerId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getWorkerProfessionsAsString(long workerId) {
        LOGGER.info("Getting Worker Professions for Worker {} as String", workerId);

        ValidationUtils.checkWorkerId(workerId);

        List<Profession> professions = getWorkerProfessions(workerId);
        StringBuilder professionsString = new StringBuilder();
        for (Profession profession : professions) {
            if (professionsString.length() > 0) {
                professionsString.append(", ");
            }
            professionsString.append(profession.getProfession().name());
        }
        return professionsString.toString();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public String createURLForProfessionFilter(String professions, String currentUrl, long neighborhoodId) {
        LOGGER.info("Creating URL for Profession Filter");

        ValidationUtils.checkNeighborhoodId(neighborhoodId);

        // Extract the base URL (path) without query parameters
        String baseUrl;
        int queryIndex = currentUrl.indexOf('?');
        if (queryIndex != -1) {
            baseUrl = currentUrl.substring(0, queryIndex);
        } else {
            baseUrl = currentUrl;
        }

        String[] professionArray = professions.split(",");

        StringBuilder queryString = new StringBuilder();

        for (String profession : professionArray) {
            if (!profession.trim().isEmpty()) { // Skip empty tags
                if (queryString.length() > 0) {
                    queryString.append("&"); // Add '&' between tags
                }
                queryString.append("profession=").append(profession.trim()); // Append each tag
            }
        }

        String formattedQueryString = queryString.toString();

        if (!formattedQueryString.isEmpty()) {
            // If there are tags to add, append them using '?' or '&' as separator
            char separator = baseUrl.contains("?") ? '&' : '?';
            return baseUrl + separator + formattedQueryString;
        } else {
            return baseUrl;
        }
    }
}
