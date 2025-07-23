package com.example.hibernate.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.example.hibernate.custome.DepartmentlEntityPersister;
import org.hibernate.annotations.Persister;

@Entity
@Table(name = "department")
//@Persister(impl = DepartmentlEntityPersister.class)
@Access(AccessType.FIELD)
public class Department
{
    @org.hibernate.annotations.Type(type = "com.example.hibernate.model.DepartmentIdType")
    @Id
    private DepartmentIdImpl id;
    @Column
    private String name;
    @Column
    private int category;

    public Department() {}
    public Department(String name, int category) {
        this.name = name;
        this.category = category;
    }

    public DepartmentIdImpl getId()
    {
        return id;
    }

    public void setId(DepartmentIdImpl id)
    {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public int getCategory()
    {
        return category;
    }

    public void setCategory(int category)
    {
        this.category = category;
    }
}