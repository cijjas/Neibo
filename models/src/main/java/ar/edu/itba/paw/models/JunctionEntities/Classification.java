package ar.edu.itba.paw.models.JunctionEntities;

import ar.edu.itba.paw.compositeKeys.ClassificationKey;
import ar.edu.itba.paw.models.MainEntities.Department;
import ar.edu.itba.paw.models.MainEntities.Product;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "products_departments")
public class Classification implements Serializable {
    @EmbeddedId
    private ClassificationKey id;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "productid")
    private Product product;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "departmentid")
    private Department department;

    public Classification() {
        this.id = new ClassificationKey();
    }

    public Classification(Product product, Department department) {
        this.id = new ClassificationKey(product.getProductId(), department.getDepartmentId());
        this.product = product;
        this.department = department;
    }

    public ClassificationKey getId() {
        return id;
    }

    public void setId(ClassificationKey id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classification that = (Classification) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
