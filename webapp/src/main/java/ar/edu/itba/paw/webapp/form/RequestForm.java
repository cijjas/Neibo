package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.constraints.AmenityURNConstraint;
import ar.edu.itba.paw.webapp.form.validation.constraints.ProductURNConstraint;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestForm {

    @NotBlank
    @Size(min = 0, max = 500)
    private String requestMessage;

    @NotNull
    @ProductURNConstraint
    private String productURN;

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

    @Override
    public String toString() {
        return "RequestForm{" +
                ", requestMessage='" + requestMessage + '\'' +
                '}';
    }
}
