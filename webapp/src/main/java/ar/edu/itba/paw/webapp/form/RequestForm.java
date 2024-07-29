package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProductURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNAuthorizationConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestForm {

    @NotBlank
    @Size(min = 0, max = 500)
    private String requestMessage;

    @NotNull
    @ProductURNConstraint
    private String productURN;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer quantity;

    @NotNull
    @UserURNAuthorizationConstraint
    private String userURN;

    public String getUserURN() {
        return userURN;
    }

    public void setUserURN(String userURN) {
        this.userURN = userURN;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getProductURN() {
        return productURN;
    }

    public void setProductURN(String productURN) {
        this.productURN = productURN;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                ", requestMessage='" + requestMessage + '\'' +
                '}';
    }
}
