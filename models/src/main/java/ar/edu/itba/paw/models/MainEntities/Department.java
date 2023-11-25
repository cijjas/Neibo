package ar.edu.itba.paw.models.MainEntities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departments_departmentid_seq")
    @SequenceGenerator(sequenceName = "departments_departmentid_seq", name = "departments_departmentid_seq", allocationSize = 1)
    private Long departmentId;

    @Column(length = 64)
    @Enumerated(EnumType.STRING)
    private ar.edu.itba.paw.enums.Department department;

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

    public ar.edu.itba.paw.enums.Department getDepartment() {
        return department;
    }

    public void setDepartment(ar.edu.itba.paw.enums.Department department) {
        this.department = department;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", department='" + department + '\'' +
                '}';
    }

    public static class Builder {
        private Long departmentId;
        private ar.edu.itba.paw.enums.Department department;

        public Builder departmentId(Long departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder department(ar.edu.itba.paw.enums.Department department) {
            this.department = department;
            return this;
        }

        public Department build() {
            return new Department(this);
        }
    }
}
