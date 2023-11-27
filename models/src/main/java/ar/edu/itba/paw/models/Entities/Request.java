package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "products_users_requests")
public class Request implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_users_requests_requestid_seq")
    @SequenceGenerator(sequenceName = "products_users_requests_requestid_seq", name = "products_users_requests_requestid_seq", allocationSize = 1)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "requestdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "fulfilled")
    private Boolean fulfilled;

    public Request() {
    }

    public Request(Request.Builder builder) {
        this.requestId = builder.requestId;
        this.product = builder.product;
        this.message = builder.message;
        this.user = builder.user;
        this.requestDate = builder.requestDate;
        this.fulfilled = builder.fulfilled;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Boolean getFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(Boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return Objects.equals(requestId, request.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    public static class Builder {
        private Long requestId;
        private Product product;
        private User user;
        private String message;
        private Date requestDate;
        private Boolean fulfilled;

        public Request.Builder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public Request.Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Request.Builder user(User user) {
            this.user = user;
            return this;
        }

        public Request.Builder message(String message) {
            this.message = message;
            return this;
        }

        public Request.Builder requestDate(Date requestDate) {
            this.requestDate = requestDate;
            return this;
        }

        public Request.Builder fulfilled(Boolean fulfilled) {
            this.fulfilled = fulfilled;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
