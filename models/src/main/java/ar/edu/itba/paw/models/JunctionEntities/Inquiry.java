package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "products_users_inquiries")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_users_inquiries_inquiryid_seq")
    @SequenceGenerator(sequenceName = "products_users_inquiries_inquiryid_seq", name = "products_users_inquiries_inquiryid_seq", allocationSize = 1)
    private Long inquiryId;

    @Column(name = "message", length = 512, nullable = false)
    private String message;

    @Column(name = "reply", length = 512)
    private String reply;

    @ManyToOne
//    @MapsId("productId")
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    private Product product;

    @ManyToOne
//    @MapsId("userId")
//    @JoinColumn(name = "userid")
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;

    public Inquiry() {
    }

    public Inquiry(Inquiry.Builder builder) {
        this.inquiryId = builder.inquiryId;
        this.product = builder.product;
        this.user = builder.user;
        this.message = builder.message;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public static class Builder {
        private Long inquiryId;
        private Product product;
        private User user;
        private String message;

        public Inquiry.Builder inquiryId(Long inquiryId) {
            this.inquiryId = inquiryId;
            return this;
        }

        public Inquiry.Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Inquiry.Builder user(User user) {
            this.user = user;
            return this;
        }

        public Inquiry.Builder message(String message) {
            this.message = message;
            return this;
        }

        public Inquiry build() {
            return new Inquiry(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inquiry)) return false;
        Inquiry inquiry = (Inquiry) o;
        return Objects.equals(inquiryId, inquiry.inquiryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inquiryId);
    }
}
