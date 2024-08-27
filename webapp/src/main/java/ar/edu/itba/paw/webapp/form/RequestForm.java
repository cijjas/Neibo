package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.ProductURNInRequestFormConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.UserURNReferenceConstraint;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestForm {

    @NotBlank
    @Size(min = 0, max = 500)
    private String requestMessage;

    @NotNull
    @ProductURNInRequestFormConstraint
    private String product;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer quantity;

    @NotNull
    @UserURNReferenceConstraint
    private String user;


    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
                "requestMessage='" + requestMessage + '\'' +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", user='" + user + '\'' +
                '}';
    }
}
