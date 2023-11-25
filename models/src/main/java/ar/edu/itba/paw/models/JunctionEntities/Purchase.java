package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "products_users_purchases")
public class Purchase implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_users_purchases_purchaseid_seq")
    @SequenceGenerator(sequenceName = "products_users_purchases_purchaseid_seq", name = "products_users_purchases_purchaseid_seq", allocationSize = 1)
    private Long purchaseId;

    @ManyToOne
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;

    @Column(name = "units")
    private Long units;

    public Purchase() {}

    public Purchase(Builder builder) {
        this.purchaseId = builder.purchaseId;
        this.product = builder.product;
        this.user = builder.user;
        this.units = builder.units;
    }

    public static class Builder {
        private Long purchaseId;
        private Product product;
        private User user;
        private Long units;

        public Builder purchaseId(Long purchaseId) {
            this.purchaseId = purchaseId;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder units(Long units) {
            this.units = units;
            return this;
        }

        public Purchase build() {
            return new Purchase(this);
        }
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return Objects.equals(purchaseId, purchase.purchaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, product, user, units);
    }
}
