package com.example.hibernate.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Department.class)
public class Department_ {
    public static volatile SingularAttribute<Department, DepartmentIdImpl> id;
    public static volatile SingularAttribute<Department, String> name;
    public static volatile SingularAttribute<Department, Integer> category;
}
