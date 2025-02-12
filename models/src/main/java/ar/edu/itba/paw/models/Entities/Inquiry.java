package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Date;
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

    @Column(name = "inquirydate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inquiryDate;

    @ManyToOne
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;


    Inquiry() {
    }

    public Inquiry(Inquiry.Builder builder) {
        this.inquiryId = builder.inquiryId;
        this.product = builder.product;
        this.user = builder.user;
        this.message = builder.message;
        this.inquiryDate = builder.inquiryDate;
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

    public Date getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(Date inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Inquiry)) return false;
        Inquiry inquiry = (Inquiry) o;
        return Objects.equals(inquiryId, inquiry.inquiryId) && Objects.equals(message, inquiry.message) && Objects.equals(reply, inquiry.reply) && Objects.equals(inquiryDate, inquiry.inquiryDate) && Objects.equals(product, inquiry.product) && Objects.equals(user, inquiry.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inquiryId, message, reply, inquiryDate, product, user);
    }

    public static class Builder {
        private Long inquiryId;
        private Product product;
        private User user;
        private String message;

        private Date inquiryDate;

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

        public Inquiry.Builder inquiryDate(Date inquiryDate) {
            this.inquiryDate = inquiryDate;
            return this;
        }

        public Inquiry build() {
            return new Inquiry(this);
        }
    }
}
