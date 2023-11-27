package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class MarkAsSoldForm {

    @NotNull
    @Range(min = 1, max = 100)
    private Integer quantity;

    @NotNull
    private Long buyerId;

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
                ", userId=" + buyerId +
                '}';
    }


}
