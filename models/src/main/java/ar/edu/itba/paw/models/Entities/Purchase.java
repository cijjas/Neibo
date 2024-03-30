package ar.edu.itba.paw.models.Entities;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    @Column(name = "purchaseDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDate;

    @Version
    @ColumnDefault("1")
    private Long version;

    public Purchase() {
    }

    public Purchase(Builder builder) {
        this.purchaseId = builder.purchaseId;
        this.product = builder.product;
        this.user = builder.user;
        this.units = builder.units;
        this.purchaseDate = builder.purchaseDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase purchase = (Purchase) o;
        return Objects.equals(purchaseId, purchase.purchaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId);
    }

    public static class Builder {
        private Long purchaseId;
        private Product product;
        private User user;
        private Long units;
        private Date purchaseDate;

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

        public Builder purchaseDate(Date purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Purchase build() {
            return new Purchase(this);
        }
    }
}
