package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PhoneRequestForm {

    @NotBlank
    @Size(min = 0, max = 500)
    private String requestMessage;

    @NotBlank
    @Size(min = 0, max = 64)
    @Pattern(regexp = "^[0-9+\\- ]*")
    private String phoneNumber;

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PhoneRequestForm{" +
                "requestMessage='" + requestMessage + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
