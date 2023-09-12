package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.services.CategorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategorizationServiceImpl implements CategorizationService {
    private final CategorizationDao categorizationDao;

    @Autowired
    public CategorizationServiceImpl(final CategorizationDao categorizationDao) {
        this.categorizationDao = categorizationDao;
    }

    @Override
    public void createCategory(long tagId, long postId) {
        categorizationDao.createCategory(tagId, postId);
    }
}
