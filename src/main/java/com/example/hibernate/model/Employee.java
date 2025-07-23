package com.example.hibernate.model;

import javax.persistence.*;
import java.util.Set;
import javax.persistence.ElementCollection;

import com.example.hibernate.custome.ExternalCollectionPersister;
import org.hibernate.annotations.Persister;

@Entity
@Table(name = "employee")
@Access(AccessType.FIELD)
public class Employee {
    @org.hibernate.annotations.Type(type = "com.example.hibernate.model.EmployeeIdType")
    @javax.persistence.Id
    private EmployeeIdImpl id;
    @Column
    private String name;
    @Column
    private double salary;

    @ElementCollection
//    @Persister(impl = ExternalCollectionPersister.class)
    private Set<WorkingDepartment> workingDepartments;

    public Employee() {}
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public EmployeeIdImpl getId()
    {
        return id;
    }

    public void setId(EmployeeIdImpl id)
    {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public Set<WorkingDepartment> getWorkingDepartments() {
        return workingDepartments;
    }

    public void setWorkingDepartments(Set<WorkingDepartment> workingDepartments) {
        this.workingDepartments = workingDepartments;
    }
}
