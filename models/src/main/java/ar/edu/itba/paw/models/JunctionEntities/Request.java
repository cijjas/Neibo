package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.compositeKeys.RequestKey;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "products_users_requests")
public class Request implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_users_requests_requestid_seq")
    @SequenceGenerator(sequenceName = "products_users_requests_requestid_seq", name = "products_users_requests_requestid_seq", allocationSize = 1)
    private Long requestId;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productid")
    private Product product;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    public Request() {
    }

    public Request(Request.Builder builder) {
        this.requestId = builder.requestId;
        this.product = builder.product;
        this.user = builder.user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request that = (Request) o;
        return requestId.equals(that.requestId);
    }

    public static class Builder {
        private Long requestId;
        private Product product;
        private User user;

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

        public Request build() {
            return new Request(this);
        }
    }
}
