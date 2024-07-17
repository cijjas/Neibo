package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.RequestURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNConstraint;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class MarkAsSoldForm {

    @NotNull
    @Range(min = 1, max = 100)
    private Integer quantity;

    @NotNull
    @UserURNConstraint
    private String buyerURN;

//    @NotNull
//    @RequestURNConstraint
//    private String requestURN;


    public String getBuyerURN() {
        return buyerURN;
    }

    public void setBuyerURN(String buyerURN) {
        this.buyerURN = buyerURN;
    }

//    public String getRequestURN() {
//        return requestURN;
//    }
//
//    public void setRequestURN(String requestURN) {
//        this.requestURN = requestURN;
//    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public String toString() {
        return "MarkAsSoldForm{" +
                "quantity=" + quantity +
                ", buyerId=" + buyerURN +
                '}';
    }


}
