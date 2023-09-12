package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.services.PostService;
import ar.edu.itba.paw.interfaces.services.TagService;
import ar.edu.itba.paw.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(final TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<Tag> getTags() {
        return tagDao.getTags();
    }

    @Override
    public Optional<List<Tag>> findTagsByPostId(long id) {
        return tagDao.findTagsByPostId(id);
    }

}