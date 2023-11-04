package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.models.compositeKeys.RequestKey;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "products_users_requests")
public class Request implements Serializable {
    @EmbeddedId
    private RequestKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productid")
    private Product product;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    public Request() {
        this.id = new RequestKey();
    }

    public Request(Product product, User user) {
        this.id = new RequestKey(product.getProductId(), user.getUserId());
        this.product = product;
        this.user = user;
    }

    public RequestKey getId() {
        return id;
    }

    public void setId(RequestKey id) {
        this.id = id;
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
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
