package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.CategorizationDao;
import ar.edu.itba.paw.interfaces.services.CategorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validation;

@Service
@Transactional
public class CategorizationServiceImpl implements CategorizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategorizationServiceImpl.class);
    private final CategorizationDao categorizationDao;

    @Autowired
    public CategorizationServiceImpl(final CategorizationDao categorizationDao) {
        this.categorizationDao = categorizationDao;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void createCategory(long tagId, long postId) {
        LOGGER.info("Creating Tag {} for Post {}", tagId, postId);

        ValidationUtils.checkTagId(tagId);
        ValidationUtils.checkPostId(postId);

        categorizationDao.createCategorization(tagId, postId);
    }
}
