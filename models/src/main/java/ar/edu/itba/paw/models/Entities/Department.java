package ar.edu.itba.paw.models.Entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departments_departmentid_seq")
    @SequenceGenerator(sequenceName = "departments_departmentid_seq", name = "departments_departmentid_seq", allocationSize = 1)
    private Long departmentId;

    @Column(length = 64)
    private String department;

    @OneToMany(mappedBy = "department")
    private Set<Product> products;

    Department() {
    }

    private Department(Builder builder) {
        this.departmentId = builder.departmentId;
        this.department = builder.department;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return Objects.equals(departmentId, that.departmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId);
    }

    public static class Builder {
        private Long departmentId;
        private String department;

        public Builder departmentId(Long departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Department build() {
            return new Department(this);
        }
    }
}
