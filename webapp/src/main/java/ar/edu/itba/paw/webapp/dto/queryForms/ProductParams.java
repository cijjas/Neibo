package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;

import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class ProductParams {

    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private long neighborhoodId;

    @QueryParam(QueryParameter.FOR_USER)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @QueryParam(QueryParameter.IN_DEPARTMENT)
    @Pattern(regexp = URIValidator.DEPARTMENT_URI_REGEX)
    private String department;

    @QueryParam(QueryParameter.WITH_STATUS)
    @Pattern(regexp = URIValidator.PRODUCT_STATUS_URI_REGEX)
    private String productStatus;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue(Constant.DEFAULT_PAGE)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue(Constant.DEFAULT_SIZE)
    private int size;

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public String getUser() {
        return user;
    }

    public String getDepartment() {
        return department;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
