package ar.edu.itba.paw.models.MainEntities;

import ar.edu.itba.paw.enums.Departments;
import ar.edu.itba.paw.enums.Professions;

import javax.persistence.*;
import java.util.List;
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
    private Departments department;

    @OneToMany(mappedBy = "department")
    private Set<Product> products;

    public Department(){}

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

    public Departments getDepartment() {
        return department;
    }

    public void setDepartment(Departments department) {
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
        private Departments department;

        public Builder departmentId(Long departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder department(Departments department) {
            this.department = department;
            return this;
        }

        public Department build() {
            return new Department(this);
        }
    }
}
