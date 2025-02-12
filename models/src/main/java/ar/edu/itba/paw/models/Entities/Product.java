package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_productid_seq")
    @SequenceGenerator(sequenceName = "products_productid_seq", name = "products_productid_seq", allocationSize = 1)
    private Long productId;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "used")
    private Boolean used;

    @Column(name = "remainingunits")
    private Long remainingUnits;

    @ManyToOne
    @JoinColumn(name = "primaryPictureId", referencedColumnName = "imageId")
    private Image primaryPicture;

    @ManyToOne
    @JoinColumn(name = "secondaryPictureId", referencedColumnName = "imageId")
    private Image secondaryPicture;

    @ManyToOne
    @JoinColumn(name = "tertiaryPictureId", referencedColumnName = "imageId")
    private Image tertiaryPicture;

    @ManyToOne
    @JoinColumn(name = "sellerId", referencedColumnName = "userId")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "departmentId", referencedColumnName = "departmentId")
    private Department department;

    @ManyToMany
    @JoinTable(name = "products_users_inquiries", joinColumns = @JoinColumn(name = "productid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> inquirers;

    @ManyToMany
    @JoinTable(name = "products_users_requests", joinColumns = @JoinColumn(name = "productid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> requesters;

    @Column(name = "creationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    Product() {
    }

    private Product(Product.Builder builder) {
        this.productId = builder.productId;
        this.name = builder.name;
        this.description = builder.description;
        this.remainingUnits = builder.remainingUnits;
        this.price = builder.price;
        this.used = builder.used;
        this.seller = builder.seller;
        this.primaryPicture = builder.primaryPicture;
        this.secondaryPicture = builder.secondaryPicture;
        this.tertiaryPicture = builder.tertiaryPicture;
        this.department = builder.department;
        this.creationDate = builder.creationDate;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRemainingUnits() {
        return remainingUnits;
    }

    public void setRemainingUnits(Long remainingUnits) {
        this.remainingUnits = remainingUnits;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isUsed() {
        return used;
    }

    public Image getPrimaryPicture() {
        return primaryPicture;
    }

    public void setPrimaryPicture(Image primaryPicture) {
        this.primaryPicture = primaryPicture;
    }

    public Image getSecondaryPicture() {
        return this.secondaryPicture;
    }

    public void setSecondaryPicture(Image secondaryPicture) {
        this.secondaryPicture = secondaryPicture;
    }

    public Image getTertiaryPicture() {
        return this.tertiaryPicture;
    }

    public void setTertiaryPicture(Image tertiaryPicture) {
        this.tertiaryPicture = tertiaryPicture;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Set<User> getInquirers() {
        return inquirers;
    }

    public void setInquirers(Set<User> inquirers) {
        this.inquirers = inquirers;
    }

    public Set<User> getRequesters() {
        return requesters;
    }

    public void setRequesters(Set<User> requesters) {
        this.requesters = requesters;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public static class Builder {
        private Long productId;
        private String name;
        private String description;
        private Double price;
        private Boolean used;
        private Long remainingUnits;
        private User seller;
        private Image primaryPicture;
        private Image secondaryPicture;
        private Image tertiaryPicture;
        private Department department;
        private Date creationDate;

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder remainingUnits(Long remainingUnits) {
            this.remainingUnits = remainingUnits;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder used(Boolean used) {
            this.used = used;
            return this;
        }

        public Builder seller(User seller) {
            this.seller = seller;
            return this;
        }

        public Builder primaryPicture(Image primaryPicture) {
            this.primaryPicture = primaryPicture;
            return this;
        }

        public Builder secondaryPicture(Image secondaryPicture) {
            this.secondaryPicture = secondaryPicture;
            return this;
        }

        public Builder tertiaryPicture(Image tertiaryPicture) {
            this.tertiaryPicture = tertiaryPicture;
            return this;
        }

        public Builder department(Department department) {
            this.department = department;
            return this;
        }

        public Builder creationDate(Date creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Product build() {
            return new Product(this);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Builder)) return false;
            Builder builder = (Builder) o;
            return Objects.equals(productId, builder.productId) && Objects.equals(name, builder.name) && Objects.equals(description, builder.description) && Objects.equals(price, builder.price) && Objects.equals(used, builder.used) && Objects.equals(remainingUnits, builder.remainingUnits) && Objects.equals(seller, builder.seller) && Objects.equals(primaryPicture, builder.primaryPicture) && Objects.equals(secondaryPicture, builder.secondaryPicture) && Objects.equals(tertiaryPicture, builder.tertiaryPicture) && Objects.equals(department, builder.department) && Objects.equals(creationDate, builder.creationDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, name, description, price, used, remainingUnits, seller, primaryPicture, secondaryPicture, tertiaryPicture, department, creationDate);
        }
    }
}
