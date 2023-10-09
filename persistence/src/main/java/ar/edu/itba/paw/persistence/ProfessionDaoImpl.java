package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.ProfessionDao;
import ar.edu.itba.paw.models.Profession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProfessionDaoImpl implements ProfessionDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String PROFESSIONS =
            "select p.* \n" +
                    "from professions p";

    @Autowired
    public ProfessionDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    // ---------------------------------------------- PROFESSIONS SELECT -----------------------------------------------

    private static final RowMapper<Profession> ROW_MAPPER = (rs, rowNum) ->
            new Profession.Builder()
                    .professionId(rs.getLong("professionid"))
                    .profession(rs.getString("profession"))
                    .build();

    @Override
    public List<Profession> getProfessions() {
        return jdbcTemplate.query(PROFESSIONS, ROW_MAPPER);
    }

}
