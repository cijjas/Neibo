package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.NeighborhoodsURIConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.ProfessionsURIConstraint;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.List;

public class WorkerParams {
    @QueryParam(QueryParameter.IN_NEIGHBORHOOD)
    @NeighborhoodsURIConstraint
    private List<String> neighborhoods;

    @QueryParam(QueryParameter.WITH_PROFESSION)
    @ProfessionsURIConstraint
    private List<String> professions;

    @QueryParam(QueryParameter.WITH_ROLE)
    @Pattern(regexp = URIValidator.WORKER_ROLE_URI_REGEX)
    private String workerRole;

    @QueryParam(QueryParameter.WITH_STATUS)
    @Pattern(regexp = URIValidator.WORKER_STATUS_URI_REGEX)
    private String workerStatus;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public List<String> getNeighborhoods() {
        return neighborhoods;
    }

    public List<String> getProfessions() {
        return professions;
    }

    public String getWorkerRole() {
        return workerRole;
    }

    public String getWorkerStatus() {
        return workerStatus;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
