package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnDelete;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class AffiliationParams {

    @QueryParam(QueryParameter.IN_NEIGHBORHOOD)
    @NotNull(groups = {OnUpdate.class, OnDelete.class})
    @Pattern(regexp = URIValidator.NEIGHBORHOOD_URI_REGEX)
    private String neighborhood;

    @QueryParam(QueryParameter.FOR_WORKER)
    @NotNull(groups = {OnUpdate.class, OnDelete.class})
    @Pattern(regexp = URIValidator.WORKER_URI_REGEX)
    private String worker;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
