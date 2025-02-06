package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class NeighborhoodParams {

    @QueryParam(QueryParameter.IS_BASE)
    private Boolean isBase;

    @QueryParam(QueryParameter.WITH_WORKER)
    @Pattern(regexp = URIValidator.WORKER_URI_REGEX)
    private String withWorker;

    @QueryParam(QueryParameter.WITHOUT_WORKER)
    @Pattern(regexp = URIValidator.WORKER_URI_REGEX)
    private String withoutWorker;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public Boolean getBase() {
        return isBase;
    }

    public String getWithWorker() {
        return withWorker;
    }

    public String getWithoutWorker() {
        return withoutWorker;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
