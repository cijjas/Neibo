package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingRequestConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ExistingUserConstraint;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class MarkAsSoldForm {

    @NotNull
    @Range(min = 1, max = 100)
    private Integer quantity;

    @NotNull
    @ExistingUserConstraint
    private Long buyerId;

    @NotNull
    @ExistingRequestConstraint
    private Long requestId;


    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public String toString() {
        return "MarkAsSoldForm{" +
                "quantity=" + quantity +
                ", buyerId=" + buyerId +
                ", requestId=" + requestId +
                '}';
    }


}
