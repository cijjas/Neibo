package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
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
    @JoinColumn(name = "buyerId", referencedColumnName = "userId")
    private User buyer;

    //junction tables:
    @ManyToMany
    @JoinTable(name = "users_products_inquiries", joinColumns = @JoinColumn(name = "productid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> inquirers;

    @ManyToMany
    @JoinTable(name = "users_products_requests", joinColumns = @JoinColumn(name = "productid"), inverseJoinColumns = @JoinColumn(name = "userid"))
    private Set<User> requesters;

    @ManyToMany
    @JoinTable(name = "products_departments", joinColumns = @JoinColumn(name = "productid"), inverseJoinColumns = @JoinColumn(name = "departmentid"))
    private Set<Department> departments;

    public Product() {
        // Default constructor
    }

    private Product(Product.Builder builder) {
        this.productId = builder.productId;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.used = builder.used;
        this.seller = builder.seller;
    }

    public Long getProductId() {
        return productId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Image getPrimaryPicture() {
        return primaryPicture;
    }

    public void setPrimaryPicture(Image primaryPicture) {
        this.primaryPicture = primaryPicture;
    }

    public Image getSecondaryPicture() {
        return secondaryPicture;
    }

    public void setSecondaryPicture(Image secondaryPicture) {
        this.secondaryPicture = secondaryPicture;
    }

    public Image getTertiaryPicture() {
        return tertiaryPicture;
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

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
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

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public static class Builder {
        private Long productId;
        private String name;
        private String description;
        private double price;
        private boolean used;
        private User seller;
        private Image primaryPicture;
        private Image secondaryPicture;
        private Image tertiaryPicture;

        public Product.Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Product.Builder name(String name) {
            this.name = name;
            return this;
        }

        public Product.Builder description(String description) {
            this.description = description;
            return this;
        }

        public Product.Builder price(double price) {
            this.price = price;
            return this;
        }

        public Product.Builder used(boolean used) {
            this.used = used;
            return this;
        }

        public Product.Builder seller(User seller) {
            this.seller = seller;
            return this;
        }

        public Product.Builder primaryPicture(Image primaryPicture) {
            this.primaryPicture = primaryPicture;
            return this;
        }

        public Product.Builder secondaryPicture(Image secondaryPicture) {
            this.secondaryPicture = secondaryPicture;
            return this;
        }

        public Product.Builder tertiaryPicture(Image tertiaryPicture) {
            this.tertiaryPicture = tertiaryPicture;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
