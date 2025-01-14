package ar.edu.itba.paw.webapp.dto.forms;

import ar.edu.itba.paw.webapp.controller.constants.QueryParameter;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserTransactionPairConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.ProductURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.RequestStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.TransactionTypeURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.urn.UserURNConstraint;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@UserTransactionPairConstraint
public class RequestForm {

    @PathParam("neighborhoodId")
    @NeighborhoodIdConstraint
    private long neighborhoodId;

    @QueryParam(QueryParameter.PAGE)
    @DefaultValue("1")
    @Min(1)
    private int page;

    @QueryParam(QueryParameter.SIZE)
    @DefaultValue("10")
    @Min(1)
    private int size;

    @QueryParam(QueryParameter.REQUESTED_BY)
    @UserURNConstraint
    private String requestedBy;

    @QueryParam(QueryParameter.FOR_PRODUCT)
    @ProductURNConstraint
    private String forProduct;

    @QueryParam(QueryParameter.WITH_TYPE)
    @TransactionTypeURNConstraint
    private String withType;

    @QueryParam(QueryParameter.WITH_STATUS)
    @RequestStatusURNConstraint
    private String withStatus;

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

    public long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getForProduct() {
        return forProduct;
    }

    public void setForProduct(String forProduct) {
        this.forProduct = forProduct;
    }

    public String getWithType() {
        return withType;
    }

    public void setWithType(String withType) {
        this.withType = withType;
    }

    public String getWithStatus() {
        return withStatus;
    }

    public void setWithStatus(String withStatus) {
        this.withStatus = withStatus;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                "page=" + page +
                ", size=" + size +
                ", neighborhoodId=" + neighborhoodId +
                ", requestedBy='" + requestedBy + '\'' +
                ", forProduct='" + forProduct + '\'' +
                ", withType='" + withType + '\'' +
                ", withStatus='" + withStatus + '\'' +
                '}';
    }
}
