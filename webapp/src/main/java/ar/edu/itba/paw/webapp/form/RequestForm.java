package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class RequestForm {

    @NotBlank
    @Size(max = 500)
    private String requestMessage;


    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                ", requestMessage='" + requestMessage + '\'' +
                '}';
    }
}
