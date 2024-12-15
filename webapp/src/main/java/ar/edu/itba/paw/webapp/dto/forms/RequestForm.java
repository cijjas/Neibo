package ar.edu.itba.paw.webapp.dto.forms;

import ar.edu.itba.paw.webapp.validation.constraints.form.ProductURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.RequestStatusURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.TransactionTypeURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.NeighborhoodIdConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.specific.UserTransactionPairConstraint;

import javax.validation.constraints.Min;
import java.util.Objects;

// Anotación personalizada para validaciones cruzadas
@UserTransactionPairConstraint
public class RequestForm {

    @Min(1)
    private int page = 1;

    @Min(1)
    private int size = 10;

    @NeighborhoodIdConstraint
    private long neighborhoodId;

    @UserURNConstraint
    private String user;

    @ProductURNConstraint
    private String product;

    @TransactionTypeURNConstraint
    private String type;

    @RequestStatusURNConstraint
    private String status;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
