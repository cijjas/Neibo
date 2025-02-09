package ar.edu.itba.paw.webapp.dto.queryForms;

import ar.edu.itba.paw.webapp.controller.constants.Constant;
import ar.edu.itba.paw.webapp.controller.constants.PathParameter;
import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.constraints.UserTransactionPairConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@UserTransactionPairConstraint
public class RequestParams {

    @PathParam(PathParameter.NEIGHBORHOOD_ID)
    private long neighborhoodId;

    @QueryParam(QueryParameter.REQUESTED_BY)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @QueryParam(QueryParameter.FOR_PRODUCT)
    @Pattern(regexp = URIValidator.PRODUCT_URI_REGEX)
    private String product;

    @QueryParam(QueryParameter.WITH_TYPE)
    @Pattern(regexp = URIValidator.TRANSACTION_TYPE_URI_REGEX)
    private String transactionType;

    @QueryParam(QueryParameter.WITH_STATUS)
    @Pattern(regexp = URIValidator.REQUEST_STATUS_URI_REGEX)
    private String requestStatus;

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

    public String getProduct() {
        return product;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
